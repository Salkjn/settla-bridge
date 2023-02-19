package de.settla.economy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import de.settla.economy.Transfer.TransferFailure;
import de.settla.economy.accounts.AdminShop;
import de.settla.economy.accounts.AdminShopHandler;
import de.settla.economy.accounts.BeamAccount;
import de.settla.economy.accounts.BeamAccountHandler;
import de.settla.economy.accounts.GuildAccount;
import de.settla.economy.accounts.GuildAccountHandler;
import de.settla.economy.accounts.HeadHunterAccount;
import de.settla.economy.accounts.HeadHunterAccountHandler;
import de.settla.economy.accounts.KillsAccount;
import de.settla.economy.accounts.KillsAccountHandler;
import de.settla.economy.accounts.Purse;
import de.settla.economy.accounts.PurseHandler;
import de.settla.economy.accounts.ServerAccount;
import de.settla.economy.accounts.ServerAccountHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storage;
import de.settla.utilities.storage.StringTuple;
import de.settla.utilities.storage.TupleList;
import net.md_5.bungee.api.ProxyServer;

public class GlobalEconomy extends Economy {

	private final Map<Class<? extends AccountHandler<?, ?>>, Function<Object, ? extends Account<?>>> defaultVaults = new HashMap<>();

	private final Map<Class<?>, Storage<AccountHandler<?, ?>>> accountHandlers = new HashMap<>();
	
	public GlobalEconomy(SakkoProtocol protocol, Currency currency) {
		super(protocol, currency);
		initAnswers();

		addAccountHandler("server", "accounts", s -> new ServerAccountHandler(s), ServerAccountHandler.class);
		addAccountHandler("adminshop", "accounts", s -> new AdminShopHandler(s), AdminShopHandler.class);
		addAccountHandler("headhunter", "accounts", s -> new HeadHunterAccountHandler(s),
				HeadHunterAccountHandler.class);
		addAccountHandler("purse", "accounts", s -> new PurseHandler(s), PurseHandler.class);
		addAccountHandler("kills", "accounts", s -> new KillsAccountHandler(s), KillsAccountHandler.class);
		addAccountHandler("guild", "accounts", s -> new GuildAccountHandler(s), GuildAccountHandler.class);
		addAccountHandler("beam", "accounts", s -> new BeamAccountHandler(s),
				BeamAccountHandler.class);

		addDefaultVault(s -> new GuildAccount(s, 0, 1000000000L), GuildAccountHandler.class);
		addDefaultVault(s -> new Purse(s, 0, 10000000L), PurseHandler.class);
		addDefaultVault(s -> new ServerAccount(s, 0, 0), ServerAccountHandler.class);
		addDefaultVault(s -> new HeadHunterAccount(s, 0, 0), HeadHunterAccountHandler.class);
		addDefaultVault(s -> new AdminShop(s, 0, 0), AdminShopHandler.class);
		addDefaultVault(s -> new KillsAccount(s, 0, 0), KillsAccountHandler.class);
		addDefaultVault(s -> new BeamAccount(s, 0, 1000L), BeamAccountHandler.class);

	}

	@SuppressWarnings("unchecked")
	public <M, T extends Account<M>, C extends AccountHandler<M, T>> void addDefaultVault(Function<M, T> function,
			Class<C> clazz) {
		defaultVaults.put(clazz, (Function<Object, ? extends Account<?>>) function);
	}

	@SuppressWarnings("unchecked")
	private void initAnswers() {

		getProtocol().answer("get_top", answer -> {

			int size = answer.getQuestion("size", Integer.class);
			Class<? extends AccountHandler<?, ?>> accountHandler = answer.getQuestion("accountHandler", Class.class);

			return answer.answer().put("top", getTop(accountHandler, size));
		});

		getProtocol().answer("get_balance", answer -> {

			Class<?> clazz = answer.getQuestion("clazz", Class.class);
			Class<? extends AccountHandler<?, ?>> accountHandler = answer.getQuestion("accountHandler", Class.class);
			Object id = answer.getQuestion("id", clazz);
			return answer.answer().put("balance", getBalance(accountHandler, id, clazz), Long.class);
		});

		getProtocol().answer("exists", answer -> {

			Class<?> clazz = answer.getQuestion("clazz", Class.class);
			Class<? extends AccountHandler<?, ?>> accountHandler = answer.getQuestion("accountHandler", Class.class);
			Object id = answer.getQuestion("id", clazz);

			return answer.answer().put("bool", exists(accountHandler, id, clazz), Boolean.class);
		});

		getProtocol().answer("transfer", answer -> {

			Class<?> fromClass = answer.getQuestion("from_class", Class.class);
			Class<?> toClass = answer.getQuestion("to_class", Class.class);
			Class<? extends AccountHandler<?, ?>> fromAccountHandlerClass = answer.getQuestion("from_handler",
					Class.class);
			Class<? extends AccountHandler<?, ?>> toAccountHandlerClass = answer.getQuestion("to_handler", Class.class);
			Object from = answer.getQuestion("from", fromClass);
			Object to = answer.getQuestion("to", toClass);
			long b = answer.getQuestion("balance", Long.class);

			return answer.answer().put("answer",
					transfer(fromAccountHandlerClass, from, fromClass, toAccountHandlerClass, to, toClass, b));
		});

		getProtocol().answer("fill", answer -> {

			Class<?> fromClass = answer.getQuestion("from_class", Class.class);
			Class<?> toClass = answer.getQuestion("to_class", Class.class);
			Class<? extends AccountHandler<?, ?>> fromAccountHandlerClass = answer.getQuestion("from_handler",
					Class.class);
			Class<? extends AccountHandler<?, ?>> toAccountHandlerClass = answer.getQuestion("to_handler", Class.class);
			Object from = answer.getQuestion("from", fromClass);
			Object to = answer.getQuestion("to", toClass);
			long b = answer.getQuestion("balance", Long.class);

			return answer.answer().put("answer",
					fill(fromAccountHandlerClass, from, fromClass, toAccountHandlerClass, to, toClass, b));
		});

	}

	public void save() {
		accountHandlers.values().forEach(s -> s.run());
	}

	@SuppressWarnings("unchecked")
	public <T, A extends Account<T>, S extends AccountHandler<T, A>> void addAccountHandler(String name, String path,
			Function<String, S> serialFunction, Class<S> clazz) {
		Database<S> database = new Database<>(name, new File("plugins/SettlaBridge/" + path + "/" + name + ".data"), serialFunction, clazz);
		Storage<S> store = new Storage<>(database);
		ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), store, 0, 3,
				TimeUnit.MINUTES);
		accountHandlers.put(clazz, (Storage<AccountHandler<?, ?>>) store);
	}

	public <A extends AccountHandler<?, ?>> A getAccountHandler(Class<A> clazz) {
		Storage<AccountHandler<?, ?>> account = accountHandlers.get(clazz);
		if (account == null)
			return null;
		return clazz.cast(account.object());
	}

	public <Id, A extends Account<Id>, H extends AccountHandler<Id, A>> long getBalance(
			Class<? extends AccountHandler<?, ?>> accountHandler, Object id, Class<?> clazz) {
		AccountHandler<?, ?> handler = getAccountHandler(accountHandler);
		if (handler == null)
			return 0L;
		synchronized (handler.lock()) {
			Account<?> account = handler.accounts().get(id);
			if (account == null)
				return 0L;
			return account.getWrappedBalance();
		}
	}

	public <Id, A extends Account<Id>, H extends AccountHandler<Id, A>> boolean exists(
			Class<? extends AccountHandler<?, ?>> accountHandler, Object id, Class<?> clazz) {
		AccountHandler<?, ?> handler = getAccountHandler(accountHandler);
		if (handler == null)
			return false;
		synchronized (handler.lock()) {
			Account<?> account = handler.accounts().get(id);
			if (account == null)
				return false;
			return true;
		}
	}

	public <Id, A extends Account<Id>, H extends AccountHandler<Id, A>> boolean delete(Class<? extends AccountHandler<?, ?>> accountHandler, Object id, Class<?> clazz) {
		AccountHandler<?, ?> handler = getAccountHandler(accountHandler);
		if (handler == null)
			return false;
		synchronized (handler.lock()) {
			handler.setDirty(true);
			Account<?> account = handler.accounts().remove(id);
			if (account == null)
				return false;
			return true;
		}
	}
	
	public <From, FromAccount extends Account<From>, FromAccountHandler extends AccountHandler<From, FromAccount>, To, ToAccount extends Account<To>, ToAccountHandler extends AccountHandler<To, ToAccount>> Transfer transfer(
			Class<? extends AccountHandler<?, ?>> fromAccountHandlerClass, Object from, Class<?> fromClass,
			Class<? extends AccountHandler<?, ?>> toAccountHandlerClass, Object to, Class<?> toClass, long balance) {
		AccountHandler<?, ?> fromAccountHandler = getAccountHandler(fromAccountHandlerClass);
		AccountHandler<?, ?> toAccountHandler = getAccountHandler(toAccountHandlerClass);

		Transfer transfer = new Transfer();

		if (fromAccountHandler != null && toAccountHandler != null) {
			synchronized (fromAccountHandler.lock()) {
				synchronized (toAccountHandler.lock()) {

					if (from != null && to != null) {
						Account<?> fromAccount = fromAccountHandler.accounts().get(from);

						if (fromAccount == null) {
							fromAccount = defaultVaults.get(fromAccountHandlerClass).apply(from);
							fromAccountHandler.put(fromAccount);
						}

						Account<?> toAccount = toAccountHandler.accounts().get(to);

						if (toAccount == null) {
							toAccount = defaultVaults.get(toAccountHandlerClass).apply(to);
							toAccountHandler.put(toAccount);
						}

						if (fromAccount != null && toAccount != null) {
							long from_wb = fromAccount.getWrappedBalance() - balance;
							long to_wb = toAccount.getWrappedBalance() + balance;
							transfer.start(fromAccount.getWrappedBalance(), toAccount.getWrappedBalance());

							if (!fromAccount.isUnlimitedAccount() && fromAccount.toHight(from_wb)) {
								transfer.failure(TransferFailure.FROM_MAXIMAL_REACHED);
							} else if (!fromAccount.isUnlimitedAccount() && fromAccount.toLow(from_wb)) {
								transfer.failure(TransferFailure.FROM_MINIMAL_REACHED);
							} else if (!toAccount.isUnlimitedAccount() && toAccount.toHight(to_wb)) {
								transfer.failure(TransferFailure.TO_MAXIMAL_REACHED);
							} else if (!toAccount.isUnlimitedAccount() && toAccount.toLow(to_wb)) {
								transfer.failure(TransferFailure.TO_MINIMAL_REACHED);
							} else {
								fromAccount.setWrappedBalance(from_wb);
								toAccount.setWrappedBalance(to_wb);
								transfer.success();
							}
							transfer.change(fromAccount.getWrappedBalance(), toAccount.getWrappedBalance());
							
							
							if(transfer.isSuccess() && transfer.change() != 0) {
								Transaction fromTran = new Transaction(toAccount, transfer.change(), transfer.change() < 0);
								Transaction toTran = new Transaction(fromAccount, transfer.change(), transfer.change() > 0);
								fromAccount.addTransaction(fromTran);
								toAccount.addTransaction(toTran);
							}
							
							
						} else {
							transfer.failure(TransferFailure.UNREGISTERED_USER);
						}
					} else {
						transfer.failure(TransferFailure.UNREGISTERED_USER);
					}
				}
			}
		} else {
			transfer.failure(TransferFailure.UNREGISTERED_ACCOUNT_HANDLER);
		}
		return transfer;
	}

	public <From, FromAccount extends Account<From> & UnlimitedAccount, FromAccountHandler extends AccountHandler<From, FromAccount>, To, ToAccount extends Account<To>, ToAccountHandler extends AccountHandler<To, ToAccount>> Transfer fill(
			Class<? extends AccountHandler<?, ?>> fromAccountHandlerClass, Object from, Class<?> fromClass,
			Class<? extends AccountHandler<?, ?>> toAccountHandlerClass, Object to, Class<?> toClass, long balance) {
		long b = balance;
		AccountHandler<?, ?> fromAccountHandler = getAccountHandler(fromAccountHandlerClass);
		AccountHandler<?, ?> toAccountHandler = getAccountHandler(toAccountHandlerClass);
		Transfer transfer = new Transfer();
		if (fromAccountHandler != null && toAccountHandler != null) {
			synchronized (fromAccountHandler.lock()) {
				synchronized (toAccountHandler.lock()) {

					if (from != null && to != null) {
						Account<?> fromAccount = fromAccountHandler.accounts().get(from);

						if (fromAccount == null) {
							fromAccount = defaultVaults.get(fromAccountHandlerClass).apply(from);
							fromAccountHandler.put(fromAccount);
						}

						Account<?> toAccount = toAccountHandler.accounts().get(to);

						if (toAccount == null) {
							toAccount = defaultVaults.get(toAccountHandlerClass).apply(to);
							toAccountHandler.put(toAccount);
						}
						if (fromAccount != null && toAccount != null) {
							long to_wb = toAccount.getWrappedBalance() + b;
							transfer.start(fromAccount.getWrappedBalance(), toAccount.getWrappedBalance());

							if (toAccount.toHight(to_wb)) {
								fromAccount.addWrappedBalance(
										toAccount.getWrappedBalance() - toAccount.getMaximumBalance());
								toAccount.setWrappedBalance(toAccount.getMaximumBalance());
								transfer.failure(TransferFailure.FILL_UP_TO_TOP);
							} else if (toAccount.toLow(to_wb)) {
								fromAccount.addWrappedBalance(
										toAccount.getWrappedBalance() - toAccount.getMinimumBalance());
								toAccount.setWrappedBalance(toAccount.getMinimumBalance());
								transfer.failure(TransferFailure.FILL_UP_TO_BOTTOM);
							} else {
								fromAccount.addWrappedBalance(-b);
								toAccount.addWrappedBalance(b);
								transfer.success();
							}
							transfer.change(fromAccount.getWrappedBalance(), toAccount.getWrappedBalance());
							
							
							if(transfer.isSuccess() && transfer.change() != 0) {
								Transaction fromTran = new Transaction(toAccount, transfer.change(), transfer.change() < 0);
								Transaction toTran = new Transaction(fromAccount, transfer.change(), transfer.change() > 0);
								fromAccount.addTransaction(fromTran);
								toAccount.addTransaction(toTran);
							}
							
							
						} else {
							transfer.failure(TransferFailure.UNREGISTERED_USER);
						}
					} else {
						transfer.failure(TransferFailure.UNREGISTERED_USER);
					}
				}
			}
		} else {
			transfer.failure(TransferFailure.UNREGISTERED_ACCOUNT_HANDLER);
		}
		
		return transfer;
	}
	
	
	//TODO CACHE!
	public <A extends AccountHandler<?, ?>> TupleList getTop(Class<A> accountHandler, int size) {
		AccountHandler<?, ?> handler = getAccountHandler(accountHandler);
		TupleList list = new TupleList(new ArrayList<>());
		if (handler == null) {

		} else {
			synchronized (handler.lock()) {
				handler.accounts().values().stream()
						.sorted((a, b) -> Long.compare(b.getWrappedBalance(), a.getWrappedBalance())).limit(size)
						.forEach(a -> {
							list.list().add(new StringTuple(a.id() + "",
									StaticParser.unparse(a.getWrappedBalance(), Long.class)));
						});
			}
		}
		return list;
	}

}
