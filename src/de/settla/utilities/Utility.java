package de.settla.utilities;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.settla.economy.Transaction;
import de.settla.economy.TransactionCache;
import de.settla.economy.Transfer;
import de.settla.economy.accounts.AdminShop;
import de.settla.economy.accounts.AdminShopHandler;
import de.settla.economy.accounts.BeamAccount;
import de.settla.economy.accounts.BeamAccountHandler;
import de.settla.economy.accounts.Purse;
import de.settla.economy.accounts.PurseHandler;
import de.settla.economy.accounts.ServerAccount;
import de.settla.economy.accounts.ServerAccountHandler;
import de.settla.global.kits.GlobalKitData;
import de.settla.global.warp.WarpPoint;
import de.settla.global.warp.WarpPoints;
import de.settla.local.kits.LocalKitData;
import de.settla.local.lobby.LobbyWildness;
import de.settla.local.npc.NpcData;
import de.settla.local.npc.NpcDatas;
import de.settla.local.portals.PortalRegion;
import de.settla.local.portals.PortalsWildness;
import de.settla.utilities.local.playerdata.PlayerCollection;
import de.settla.utilities.sakko.protocol.SakkoAnswer;
import de.settla.utilities.sakko.protocol.SakkoQuestion;
import de.settla.utilities.storage.Storable.Memory;
import de.settla.utilities.storage.StringTuple;
import de.settla.utilities.storage.TupleList;
import de.settla.utilities.storage.UniqueIdBlock;

public class Utility {


	public static final int DEFAULT_PORT = 28080;
	public static final String DEFAULT_ADDRESS = "localhost";

	public static void init() {

		Memory.register(TupleList.class, map -> new TupleList(map));
		Memory.register(StringTuple.class, map -> new StringTuple(map));

		Memory.register(SakkoAnswer.class, map -> new SakkoAnswer(map));
		Memory.register(SakkoQuestion.class, map -> new SakkoQuestion(map));
		Memory.register(Transfer.class, map -> new Transfer(map));
		Memory.register(Transaction.class, map -> new Transaction(map));
		Memory.register(TransactionCache.class, map -> new TransactionCache(map));

		Memory.register(Purse.class, map -> new Purse(map));
		Memory.register(PurseHandler.class, map -> new PurseHandler(map));

		Memory.register(AdminShop.class, map -> new AdminShop(map));
		Memory.register(AdminShopHandler.class, map -> new AdminShopHandler(map));

		Memory.register(BeamAccount.class, map -> new BeamAccount(map));
		Memory.register(BeamAccountHandler.class, map -> new BeamAccountHandler(map));

		Memory.register(ServerAccount.class, map -> new ServerAccount(map));
		Memory.register(ServerAccountHandler.class, map -> new ServerAccountHandler(map));

		Memory.register(UniqueIdBlock.class, map -> new UniqueIdBlock(map));

		Memory.register(WarpPoint.class, map -> new WarpPoint(map));
		Memory.register(WarpPoints.class, map -> new WarpPoints(map));

		Memory.register(NpcData.class, map -> new NpcData(map));
		Memory.register(NpcDatas.class, map -> new NpcDatas(map));

		Memory.register(GlobalKitData.class, map -> new GlobalKitData(map));
		Memory.register(LocalKitData.class, map -> new LocalKitData(map));

		Memory.register(PortalRegion.class, map -> new PortalRegion(map));
		Memory.register(PortalsWildness.class, map -> new PortalsWildness(map));

		Memory.register(LobbyWildness.class, map -> new LobbyWildness(map));
		Memory.register(TimeValue.class, map -> new TimeValue(map));

		Memory.register(PlayerCollection.class, map -> new PlayerCollection(map));
		
		Memory.register(TupleList.class, map -> new TupleList(map));
		Memory.register(StringTuple.class, map -> new StringTuple(map));
		Memory.register(UniqueIdBlock.class, map -> new UniqueIdBlock(map));
		Memory.register(PlayerCollection.class, map -> new PlayerCollection(map));
		Memory.register(NpcData.class, map -> new NpcData(map));
		Memory.register(NpcDatas.class, map -> new NpcDatas(map));
		
		
		
	}

	public static String timeToString(long time) {
		final long leftTime = time;
		long days = TimeUnit.MILLISECONDS.toDays(leftTime);
		if (days == 0) {
			long hours = TimeUnit.MILLISECONDS.toHours(leftTime);
			if (hours == 0) {
				long minutes = TimeUnit.MILLISECONDS.toMinutes(leftTime);
				if (minutes == 0) {
					long seconds = TimeUnit.MILLISECONDS.toSeconds(leftTime);
					return seconds == 1 ? seconds + " Sekunde" : seconds + " Sekunden";
				} else {
					return minutes == 1 ? minutes + " Minute" : minutes + " Minuten";
				}
			} else {
				return hours == 1 ? hours + " Stunde" : hours + " Stunden";
			}
		} else {
			return days == 1 ? days + " Tag" : days + " Tage";
		}
	}

	public static String timeToFancyString(long time) {
		final long leftTime = time;
		long days = TimeUnit.MILLISECONDS.toDays(leftTime);
		if (days == 0) {
			long hours = TimeUnit.MILLISECONDS.toHours(leftTime);
			if (hours == 0) {
				long minutes = TimeUnit.MILLISECONDS.toMinutes(leftTime);
				if (minutes == 0) {
					long seconds = TimeUnit.MILLISECONDS.toSeconds(leftTime);
					return seconds == 1 ? "Jede Sekunde" : "Alle " + seconds + " Sekunden";
				} else {
					return minutes == 1 ? "Jede Minute" : "Alle " + minutes + " Minuten";
				}
			} else {
				return hours == 1 ? "Jede Stunde" : "Alle " + hours + " Stunden";
			}
		} else {
			return days == 1 ? "Jeden Tag" : "Alle " + days + " Tage";
		}
	}

	private static Function<String, Long> parseTimeUnit = input -> {
		switch (input.trim().toLowerCase()) {
		case "s":
		case "sec":
		case "secund":
		case "secunds":
		case "sekunde":
		case "sekunden":
			return 1000L;
		case "m":
		case "min":
		case "minute":
		case "minuten":
		case "minutes":
			return 1000L * 60L;
		case "h":
		case "stunde":
		case "stunden":
		case "hours":
		case "hour":
			return 1000L * 60L * 60L;
		case "d":
		case "day":
		case "tage":
		case "tag":
		case "days":
			return 1000L * 60L * 60L * 24L;
		case "year":
		case "y":
		case "jahr":
		case "jahre":
		case "years":
			return 365L * 1000L * 60L * 60L * 24L;
		default:
			return null;
		}
	};

	public static Long timeFromString(long duration, String timeUnit) {
		Long time = parseTimeUnit.apply(timeUnit);
		return time == null ? null : duration * time;
	}

	private static final String time_format_string = "(\\d+)([A-z]+)";
	private static Pattern time_format;
	static {
		try {
			time_format = Pattern.compile(time_format_string, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
	}

	public static Long timeFromString(String time) {
		Matcher m = time_format.matcher(time);
		
		m.find();
		
		if (m.groupCount() == 2) {
			
			try {
				String s1 = m.group(1);
				String s2 = m.group(2);
				try {
					long duration = Long.parseLong(s1);
					return timeFromString(duration, s2);
				} catch (NumberFormatException e) {
					return null;
				}
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return null;
	}
	
	public static String timeFromLong(long time, boolean fancy) {
		long sec = time / 1000L;
		long r_sec = sec % 60L;
		long min = sec / 60L;
		long r_min = min % 60L;
		long hours = min / 60L;
		long r_hours = hours % 24L;
		long days = hours / 24L;
		long r_days = days % 365L;
		long r_years = days / 365L;
		
		if (fancy) {
			String str = (r_years == 0 ? "" : r_years + " Jahr(e) ") + 
					(r_days == 0 ? "" : r_days + " Tag(e) ") + 
					(r_hours == 0 ? "" : r_hours + " Stunde(n) ") + 
					(r_min == 0 ? "" : r_min + " Minute(n) ") + 
					(r_sec == 0 ? "" : r_sec + " Sekunde(n)");
			if (str.endsWith(" ")) {
				str = str.substring(0, str.length() - 1);
			}
			return str;
		}
		
		return r_years + " Jahr(e) " + r_days + " Tag(e) " + r_hours + " Stunde(n) " + r_min + " Minute(n) " + r_sec + " Sekunde(n)"; 
	}

}
