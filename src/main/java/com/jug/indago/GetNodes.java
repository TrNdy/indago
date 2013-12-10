package com.jug.indago;

import java.util.List;

import org.scijava.Context;
import org.scijava.InstantiableException;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;

import com.jug.indago.influit.nodes.InfluitNode;

public class GetNodes {

	public static void main( final String[] args ) throws InstantiableException {
		final Context context = new Context( PluginService.class );
//		final PluginIndex pluginIndex = context.getPluginIndex();
//		final List< PluginInfo< ? > > plugins = pluginIndex.get( InfluitNode.class );
//		System.out.println( plugins.size() );
//		for ( final PluginInfo< ? > info : plugins )
//		{
//			System.out.println( info );
//		}

		final PluginService pluginService = context.getService( PluginService.class );
		final List< PluginInfo< InfluitNode > > plugins = pluginService.getPluginsOfType( InfluitNode.class );
		for ( final PluginInfo< InfluitNode > info : plugins ) {
			System.out.println( info );
			final InfluitNode plugin = info.createInstance();
			System.out.println( plugin );
		}
	}

	public static List< PluginInfo< InfluitNode >> getInfluitNodePlugins() {
		final Context context = new Context( PluginService.class );
		final PluginService pluginService = context.getService( PluginService.class );
		return pluginService.getPluginsOfType( InfluitNode.class );
	}
}
