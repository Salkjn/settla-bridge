package de.settla.economy;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

import de.settla.utilities.Tuple;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.TupleList;

public class LocalEconomy extends Economy {

	public LocalEconomy(SakkoProtocol protocol, Currency currency) {
		super(protocol, currency);
	}

	public <Id, A extends Account<Id>, H extends AccountHandler<Id, A>> void getBalance(Class<H> accountHandler, Id id,
			Class<Id> clazz, DoubleConsumer answer) {
		getProtocol()
				.ask("get_balance",
						question -> question.put("id", StaticParser.unparse(id, clazz), String.class)
								.put("accountHandler", accountHandler, Class.class).put("clazz", clazz, Class.class),
						a -> {
							answer.accept(getWrapper().backward(a.getAnswer("balance", Long.class)));
						});
	}

	public <Id, A extends Account<Id>, H extends AccountHandler<Id, A>> void exists(Class<H> accountHandler, Id id,
			Class<Id> clazz, Consumer<Boolean> answer) {
		getProtocol()
				.ask("exists",
						question -> question.put("id", StaticParser.unparse(id, clazz), String.class)
								.put("accountHandler", accountHandler, Class.class).put("clazz", clazz, Class.class),
						a -> {
							answer.accept(a.getAnswer("bool", Boolean.class));
						});
	}

	public <From, FromAccount extends Account<From>, FromAccountHandler extends AccountHandler<From, FromAccount>, To, ToAccount extends Account<To>, ToAccountHandler extends AccountHandler<To, ToAccount>> void transfer(
			Class<FromAccountHandler> fromAccountHandler, From from, Class<From> from_class,
			Class<ToAccountHandler> toAccountHandler, To to, Class<To> to_class, double balance,
			Consumer<Transfer> answer) {
		getProtocol().ask("transfer",
				question -> question.put("from", StaticParser.unparse(from, from_class), String.class)
						.put("to", StaticParser.unparse(to, to_class), String.class)
						.put("from_class", from_class, Class.class).put("to_class", to_class, Class.class)
						.put("from_handler", fromAccountHandler, Class.class)
						.put("to_handler", toAccountHandler, Class.class)
						.put("balance", getWrapper().forward(balance), Long.class),
				a -> {
					answer.accept(a.getStorableAnswer("answer", Transfer.class));
				});
	}

	public <From, FromAccount extends Account<From> & UnlimitedAccount, FromAccountHandler extends AccountHandler<From, FromAccount>, To, ToAccount extends Account<To>, ToAccountHandler extends AccountHandler<To, ToAccount>> void fill(
			Class<FromAccountHandler> fromAccountHandler, From from, Class<From> from_class,
			Class<ToAccountHandler> toAccountHandler, To to, Class<To> to_class, double balance,
			Consumer<Transfer> answer) {
		getProtocol().ask("fill", question -> question.put("from", StaticParser.unparse(from, from_class), String.class)
				.put("to", StaticParser.unparse(to, to_class), String.class).put("from_class", from_class, Class.class)
				.put("to_class", to_class, Class.class).put("from_handler", fromAccountHandler, Class.class)
				.put("to_handler", toAccountHandler, Class.class)
				.put("balance", getWrapper().forward(balance), Long.class), a -> {
					answer.accept(a.getStorableAnswer("answer", Transfer.class));
				});
	}

	public <A extends AccountHandler<?, ?>> void getTop(Class<A> accountHandler, int size,
			Consumer<List<Tuple<String, Double>>> answer) {
		getProtocol().ask("get_top", question -> question.put("accountHandler", accountHandler, Class.class).put("size",
				size, Integer.class), a -> {
					TupleList list = a.getStorableAnswer("top", TupleList.class);
					answer.accept(list.list().stream()
							.map(s -> new Tuple<>(s.getFirst(),
									getWrapper().backward(StaticParser.parse((String) s.getSecond(), Long.class))))
							.collect(Collectors.toList()));
				});
	}

}
