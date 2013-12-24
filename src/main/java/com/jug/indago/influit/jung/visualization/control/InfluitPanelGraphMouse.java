package com.jug.indago.influit.jung.visualization.control;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.jug.indago.influit.edges.InfluitEdge;
import com.jug.indago.influit.edges.InfluitEdgeFactory;
import com.jug.indago.influit.nodes.InfluitNode;
import com.jug.indago.influit.nodes.InfluitNodeVertexFactory;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.LabelEditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

/**
 * This class handles the mouse control of the InfluitPanel.
 * Adapted from the EditModalGraphMouse that comes with JUNG.
 *
 * @author jug
 */
public class InfluitPanelGraphMouse extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable {

	protected InfluitNodeVertexFactory vertexFactory;
	protected InfluitEdgeFactory edgeFactory;
	protected InfluitPanelGraphMousePlugin editingPlugin;
	protected LabelEditingGraphMousePlugin< InfluitNode, InfluitEdge > labelEditingPlugin;
	protected InfluitPanelEditingPopupGraphMousePlugin popupEditingPlugin;
	protected AnnotatingGraphMousePlugin< InfluitNode, InfluitEdge > annotatingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext< InfluitNode, InfluitEdge > rc;

	/**
	 * create an instance with default values
	 *
	 */
	public InfluitPanelGraphMouse( final RenderContext< InfluitNode, InfluitEdge > rc, final InfluitNodeVertexFactory vertexFactory, final InfluitEdgeFactory edgeFactory ) {
		this( rc, vertexFactory, edgeFactory, 1.1f, 1 / 1.1f );
	}

	/**
	 * create an instance with passed values
	 *
	 * @param in
	 *            override value for scale in
	 * @param out
	 *            override value for scale out
	 */
	public InfluitPanelGraphMouse( final RenderContext< InfluitNode, InfluitEdge > rc, final InfluitNodeVertexFactory vertexFactory, final InfluitEdgeFactory edgeFactory, final float in, final float out ) {
		super( in, out );
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		loadPlugins();
		setModeKeyListener( new ModeKeyAdapter( this ) );
	}

	/**
	 * create the plugins, and load the plugins for mouse modes
	 *
	 */
	@Override
	protected void loadPlugins() {

		// our own stuff
		editingPlugin = new InfluitPanelGraphMousePlugin( vertexFactory, edgeFactory );
		popupEditingPlugin = new InfluitPanelEditingPopupGraphMousePlugin( vertexFactory, edgeFactory );

		// default JUNG stuff
		pickingPlugin = new PickingGraphMousePlugin< InfluitNode, InfluitEdge >();
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin< InfluitNode, InfluitEdge >();
		translatingPlugin = new TranslatingGraphMousePlugin( InputEvent.BUTTON1_MASK );
		scalingPlugin = new ScalingGraphMousePlugin( new CrossoverScalingControl(), 0, in, out );
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		labelEditingPlugin = new LabelEditingGraphMousePlugin< InfluitNode, InfluitEdge >();
		annotatingPlugin = new AnnotatingGraphMousePlugin< InfluitNode, InfluitEdge >( rc );
		add( scalingPlugin );

		setMode( Mode.PICKING );
	}

	/**
	 * setter for the Mode.
	 */
	@Override
	public void setMode( final Mode mode ) {
		if ( this.mode != mode ) {
			fireItemStateChanged( new ItemEvent( this, ItemEvent.ITEM_STATE_CHANGED, this.mode, ItemEvent.DESELECTED ) );
			this.mode = mode;
			if ( mode == Mode.TRANSFORMING ) {
				setTransformingMode();
			} else if ( mode == Mode.PICKING ) {
				setPickingMode();
			} else if ( mode == Mode.EDITING ) {
				setEditingMode();
			} else if ( mode == Mode.ANNOTATING ) {
				setAnnotatingMode();
			}
			if ( modeBox != null ) {
				modeBox.setSelectedItem( mode );
			}
			fireItemStateChanged( new ItemEvent( this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED ) );
		}
	}

	@Override
	protected void setPickingMode() {
		remove( translatingPlugin );
		remove( rotatingPlugin );
		remove( shearingPlugin );
		remove( editingPlugin );
		remove( annotatingPlugin );
		add( pickingPlugin );
		add( animatedPickingPlugin );
		add( labelEditingPlugin );
		add( popupEditingPlugin );
	}

	@Override
	protected void setTransformingMode() {
		remove( pickingPlugin );
		remove( animatedPickingPlugin );
		remove( editingPlugin );
		remove( annotatingPlugin );
		add( translatingPlugin );
		add( rotatingPlugin );
		add( shearingPlugin );
		add( labelEditingPlugin );
		add( popupEditingPlugin );
	}

	protected void setEditingMode() {
		remove( pickingPlugin );
		remove( animatedPickingPlugin );
		remove( translatingPlugin );
		remove( rotatingPlugin );
		remove( shearingPlugin );
		remove( labelEditingPlugin );
		remove( annotatingPlugin );
		add( editingPlugin );
		add( popupEditingPlugin );
	}

	protected void setAnnotatingMode() {
		remove( pickingPlugin );
		remove( animatedPickingPlugin );
		remove( translatingPlugin );
		remove( rotatingPlugin );
		remove( shearingPlugin );
		remove( labelEditingPlugin );
		remove( editingPlugin );
		remove( popupEditingPlugin );
		add( annotatingPlugin );
	}

	/**
	 * @return the modeBox.
	 */
	@Override
	public JComboBox getModeComboBox() {
		if ( modeBox == null ) {
			modeBox = new JComboBox( new Mode[] { Mode.TRANSFORMING, Mode.PICKING, Mode.EDITING, Mode.ANNOTATING } );
			modeBox.addItemListener( getModeListener() );
		}
		modeBox.setSelectedItem( mode );
		return modeBox;
	}

	/**
	 * create (if necessary) and return a menu that will change
	 * the mode
	 *
	 * @return the menu
	 */
	@Override
	public JMenu getModeMenu() {
		if ( modeMenu == null ) {
			modeMenu = new JMenu( "MouseMode" );
//			final Icon icon = BasicIconFactory.getMenuArrowIcon();
//			modeMenu.setIcon( BasicIconFactory.getMenuArrowIcon() );
//			modeMenu.setPreferredSize( new Dimension( icon.getIconWidth() + 10, icon.getIconHeight() + 10 ) );

			final JRadioButtonMenuItem transformingButton = new JRadioButtonMenuItem( Mode.TRANSFORMING.toString() );
			transformingButton.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged( final ItemEvent e ) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						setMode( Mode.TRANSFORMING );
					}
				}
			} );

			final JRadioButtonMenuItem pickingButton = new JRadioButtonMenuItem( Mode.PICKING.toString() );
			pickingButton.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged( final ItemEvent e ) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						setMode( Mode.PICKING );
					}
				}
			} );

			final JRadioButtonMenuItem editingButton = new JRadioButtonMenuItem( Mode.EDITING.toString() );
			editingButton.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged( final ItemEvent e ) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						setMode( Mode.EDITING );
					}
				}
			} );

//			final JRadioButtonMenuItem annotatingButton = new JRadioButtonMenuItem( Mode.ANNOTATING.toString() );
//			editingButton.addItemListener( new ItemListener() {
//
//				@Override
//				public void itemStateChanged( final ItemEvent e ) {
//					if ( e.getStateChange() == ItemEvent.SELECTED ) {
//						setMode( Mode.ANNOTATING );
//					}
//				}
//			} );

			final ButtonGroup radio = new ButtonGroup();
			radio.add( transformingButton );
			radio.add( pickingButton );
			radio.add( editingButton );
//			radio.add( annotatingButton );
			transformingButton.setSelected( true );
			modeMenu.add( transformingButton );
			modeMenu.add( pickingButton );
			modeMenu.add( editingButton );
//			modeMenu.add( annotatingButton );
			modeMenu.setToolTipText( "Menu for setting Mouse Mode" );
			addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged( final ItemEvent e ) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						if ( e.getItem() == Mode.TRANSFORMING ) {
							transformingButton.setSelected( true );
						} else if ( e.getItem() == Mode.PICKING ) {
							pickingButton.setSelected( true );
						} else if ( e.getItem() == Mode.EDITING ) {
							editingButton.setSelected( true );
//						} else if ( e.getItem() == Mode.ANNOTATING ) {
//							annotatingButton.setSelected( true );
						}
					}
				}
			} );
		}
		return modeMenu;
	}

	public static class ModeKeyAdapter extends KeyAdapter {

		private char t = 't';
		private char p = 'p';
		private char e = 'e';
		private char a = 'a';
		protected ModalGraphMouse graphMouse;

		public ModeKeyAdapter( final ModalGraphMouse graphMouse ) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter( final char t, final char p, final char e, final char a, final ModalGraphMouse graphMouse ) {
			this.t = t;
			this.p = p;
			this.e = e;
			this.a = a;
			this.graphMouse = graphMouse;
		}

		@Override
		public void keyTyped( final KeyEvent event ) {
			final char keyChar = event.getKeyChar();
			if ( keyChar == t ) {
				( ( Component ) event.getSource() ).setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
				graphMouse.setMode( Mode.TRANSFORMING );
			} else if ( keyChar == p ) {
				( ( Component ) event.getSource() ).setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
				graphMouse.setMode( Mode.PICKING );
			} else if ( keyChar == e ) {
				( ( Component ) event.getSource() ).setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
				graphMouse.setMode( Mode.EDITING );
			} else if ( keyChar == a ) {
				( ( Component ) event.getSource() ).setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
				graphMouse.setMode( Mode.ANNOTATING );
			}
		}
	}

	/**
	 * @return the annotatingPlugin
	 */
	public AnnotatingGraphMousePlugin< InfluitNode, InfluitEdge > getAnnotatingPlugin() {
		return annotatingPlugin;
	}

	/**
	 * @return the editingPlugin
	 */
	public InfluitPanelGraphMousePlugin getEditingPlugin() {
		return editingPlugin;
	}

	/**
	 * @return the labelEditingPlugin
	 */
	public LabelEditingGraphMousePlugin< InfluitNode, InfluitEdge > getLabelEditingPlugin() {
		return labelEditingPlugin;
	}

	/**
	 * @return the popupEditingPlugin
	 */
	public InfluitPanelEditingPopupGraphMousePlugin getPopupEditingPlugin() {
		return popupEditingPlugin;
	}
}

