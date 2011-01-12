/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.validation.IValidationPlugin;

/**
 * @author michel
 *
 */
public class PluginInjector<T extends NeptuneIdentifiedObject>
{
	public static PluginInjector<Line> createLinePluginInjector()
	{
		return new PluginInjector<Line>();
	}
	public static PluginInjector<PTNetwork> createPTNetworkPluginInjector()
	{
		return new PluginInjector<PTNetwork>();
	}
	public static PluginInjector<Company> createCompanyPluginInjector()
	{
		return new PluginInjector<Company>();
	}
	public static PluginInjector<JourneyPattern> createJourneyPatternPluginInjector()
	{
		return new PluginInjector<JourneyPattern>();
	}
	public static PluginInjector<Route> createRoutePluginInjector()
	{
		return new PluginInjector<Route>();
	}
	public static PluginInjector<StopPoint> createStopPointPluginInjector()
	{
		return new PluginInjector<StopPoint>();
	}


	@Getter @Setter private INeptuneManager<T> manager;
	@Getter @Setter private List<IImportPlugin<T>> importPlugins;
	@Getter @Setter private List<IExportPlugin<T>> exportPlugins;
	@Getter @Setter private List<IExportPlugin<T>> exportDeletionPlugins;
	@Getter @Setter private List<IValidationPlugin<T>> validationPlugins;

	public void init()
	{
		if (importPlugins != null)
		{
			for (IImportPlugin<T> plugin : importPlugins) 
			{
				manager.addImportPlugin(plugin);
			}
		}
		if (exportPlugins != null)
		{
			for (IExportPlugin<T> plugin : exportPlugins) 
			{
				manager.addExportPlugin(plugin);
			}
		}
		if (exportDeletionPlugins != null)
		{
			for (IExportPlugin<T> plugin : exportDeletionPlugins) 
			{
				manager.addExportDeletionPlugin(plugin);
			}
		}
		if (validationPlugins != null)
		{
			for (IValidationPlugin<T> plugin : validationPlugins) 
			{
				manager.addValidationPlugin(plugin);
			}
		}
	}

}