/**
 *
 */
package com.jug.indago.influit.nodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Factory;
import org.scijava.InstantiableException;
import org.scijava.plugin.PluginInfo;

import com.jug.indago.influit.InfluitPanel;


/**
 * @author jug
 */
public class InfluitNodeVertexFactory implements Factory< InfluitNode > {

	private final InfluitPanel influitPanel;

	public InfluitNodeVertexFactory( final InfluitPanel influitPanel ) {
		this.influitPanel = influitPanel;
	}

	/**
	 * @see org.apache.commons.collections15.Factory#create()
	 */
	@Override
	public InfluitNode create() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param e
	 * @return
	 */
	public JPopupMenu getInfluitNodeSelectionPopup( final MouseEvent e ) {
		final JPopupMenu ret = new JPopupMenu( "Add..." );
		for ( final PluginInfo< InfluitNode > info : influitPanel.getNodeInfos() ) {
			final JMenuItem node = new JMenuItem( info.getLabel() );
			node.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed( final ActionEvent evt ) {
					InfluitNode instance;
					try {
						instance = info.createInstance();
						influitPanel.addNode( instance );
						influitPanel.getJungLayout().setLocation( instance, influitPanel.getJungVisualizationViewer().getRenderContext().getMultiLayerTransformer().inverseTransform( e.getPoint() ) );
						influitPanel.repaint();
					} catch ( final InstantiableException e ) {
						System.out.println( "ERROR: InfluitNode could not be instantiated!" );
						e.printStackTrace();
					}
				}
			} );
			ret.add( node );
		}
		return ret;
	}
}
