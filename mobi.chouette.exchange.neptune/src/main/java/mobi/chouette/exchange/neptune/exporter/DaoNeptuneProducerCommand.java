package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = DaoNeptuneProducerCommand.COMMAND)
public class DaoNeptuneProducerCommand implements Command, Constant {

	public static final String COMMAND = "DaoNeptuneProducerCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {

			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);
			
			InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
			
			Command export = CommandFactory.create(initialContext, NeptuneProducerCommand.class.getName());
			
			context.put(LINE, line);
			export.execute(context);
			
//			NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);
//
//			ExportableData collection = new ExportableData();
//
//			Date startDate = null;
//			if (configuration.getStartDate() != null) {
//				startDate = new Date(configuration.getStartDate().getTime());
//			}
//
//			Date endDate = null;
//			if (configuration.getEndDate() != null) {
//				endDate = new Date(configuration.getEndDate().getTime());
//			}
//
//			NeptuneDataCollector collector = new NeptuneDataCollector();
//			boolean cont = (collector.collect(collection, line, startDate, endDate));
//			LineInfo lineInfo = new LineInfo();
//			lineInfo.setName(line.getName() + " (" + line.getNumber() + ")");
//			LineStats stats = new LineStats();
//			stats.setAccessPointCount(collection.getAccessPoints().size());
//			stats.setConnectionLinkCount(collection.getConnectionLinks().size());
//			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
//			stats.setRouteCount(collection.getRoutes().size());
//			stats.setStopAreaCount(collection.getStopAreas().size());
//			stats.setTimeTableCount(collection.getTimetables().size());
//			stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());
//
//			if (cont) {
//				context.put(EXPORTABLE_DATA, collection);
//
//				ChouettePTNetworkProducer producer = new ChouettePTNetworkProducer();
//				producer.produce(context);
//
//				lineInfo.setStatus(LINE_STATE.OK);
//				stats.setLineCount(1);
//				// merge lineStats to global ones
//				LineStats globalStats = report.getStats();
//				if (globalStats == null) {
//					globalStats = new LineStats();
//					report.setStats(globalStats);
//				}
//				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
//				globalStats.setAccessPointCount(globalStats.getAccessPointCount() + stats.getAccessPointCount());
//				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
//				globalStats.setConnectionLinkCount(globalStats.getConnectionLinkCount()
//						+ stats.getConnectionLinkCount());
//				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
//						+ stats.getVehicleJourneyCount());
//				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
//						+ stats.getJourneyPatternCount());
//				globalStats.setStopAreaCount(globalStats.getStopAreaCount() + stats.getStopAreaCount());
//				globalStats.setTimeTableCount(globalStats.getTimeTableCount() + stats.getTimeTableCount());
//				result = SUCCESS;
//			} else {
//				lineInfo.setStatus(LINE_STATE.ERROR);
//				result = ERROR;
//			}
//			report.getLines().add(lineInfo);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(DaoNeptuneProducerCommand.class.getName(), new DefaultCommandFactory());
	}
}
