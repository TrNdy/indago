package com.indago.data.segmentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.indago.io.ImageSaver;

import gnu.trove.impl.Constants;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ij.ImagePlus;
import io.scif.img.IO;
import mpicbg.spim.data.XmlHelpers;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * @author tpietzsch, jug
 */
public class XmlIoLabelingPlus {

	public static final String SEGMENTLABELING_TAG = "SegmentLabeling";
	public static final String BASEPATH_TAG = "BasePath";

	public static final String INDEXIMG_TAG = "IndexImgFile";

	public static final String LABELS_TAG = "Labels";

	public static final String MAPPING_TAG = "Mapping";
	public static final String MAPPING_ENTRY_TAG = "MappingEntry";
	public static final String MAPPING_ENTRY_INDEX_TAG = "Index";
	public static final String MAPPING_ENTRY_LABELS_TAG = "Labels";

	public static final String LABELINGTREE_TAG = "LabelingForests";
	public static final String LABELINGTREE_NODE_TAG = "Node";
	public static final String LABELINGTREE_NODE_ID_TAG = "Label";
	public static final String LABELINGTREE_NODE_CHILDREN_TAG = "Children";
	public static final String LABELINGTREE_FOREST_TAG = "Forest";
	public static final String LABELINGTREE_FOREST_ROOTS_TAG = "Roots";

	public static boolean openIndexImageWithIJ = true;

	public LabelingPlus load( final String xmlFilename ) throws IOException {
		return load( new File( xmlFilename ) );
	}

	public LabelingPlus load( final File xmlFile ) throws IOException {
		final SAXBuilder sax = new SAXBuilder();
		Document doc;
		try {
			doc = sax.build( xmlFile );
		} catch ( final Exception e ) {
			throw new IOException( e );
		}
		final Element root = doc.getRootElement();

		final File basePath = loadBasePath( root, xmlFile );
		final File indexImgFile = XmlHelpers.loadPath( root, INDEXIMG_TAG, basePath );
		final Img< IntType > indexImg = openIndexImageWithIJ
				? openIntImageFastButDangerous( indexImgFile.getAbsolutePath() )
				: IO.openImgs(
						indexImgFile.getAbsolutePath(),
						new ArrayImgFactory<>( new IntType() ) ).get( 0 ).getImg();
		final LabelingPlus labelingPlus = new LabelingPlus( indexImg );
		fromXml( root, labelingPlus );
		return labelingPlus;
	}

	public void save( final LabelingPlus labelingData, final String xmlFilename )
			throws IOException {
		final File xmlFileDirectory = new File( xmlFilename ).getParentFile();
		final String indexImgFilename =
				xmlFilename.substring( 0, xmlFilename.length() - ".xml".length() ) + ".tif";
		final File indexImgFile = new File( indexImgFilename );
		final Document doc = new Document( toXml( labelingData, xmlFileDirectory, indexImgFile ) );
		final XMLOutputter xout = new XMLOutputter( Format.getPrettyFormat() );
		xout.output( doc, new FileOutputStream( xmlFilename ) );

		final Img< IntType > img = ImgView.wrap( labelingData.getLabeling().getIndexImg(), null );
		ImageSaver.saveAsTiff( indexImgFilename, img );
	}

	private void fromXml( final Element segmentLabeling, final LabelingPlus labelingPlus )
			throws IOException {
		final TIntObjectMap< LabelData > idToLabelMap =
				getIdToLabelMap( segmentLabeling, LABELS_TAG );

		final Element mapping = segmentLabeling.getChild( MAPPING_TAG );
		if ( mapping == null )
			throw new IOException( "no <" + MAPPING_TAG + "> element found." );

		final ArrayList< Set< LabelData > > labelSets = new ArrayList<>();
		for ( final Element entry : mapping.getChildren( MAPPING_ENTRY_TAG ) ) {
			final int i = XmlHelpers.getInt( entry, MAPPING_ENTRY_INDEX_TAG );
			final int[] ids = XmlHelpers.getIntArray( entry, MAPPING_ENTRY_LABELS_TAG );
			final HashSet< LabelData > labelSet = new HashSet<>( ids.length );
			for ( final int id : ids )
				labelSet.add( idToLabelMap.get( id ) );

			while ( labelSets.size() <= i )
				labelSets.add( null );
			labelSets.set( i, labelSet );
		}
		new SegmentLabelingSerialisation( labelingPlus.getLabeling().getMapping() ).setLabelSets( labelSets );

		final Element labelingtree = segmentLabeling.getChild( LABELINGTREE_TAG );
		if ( labelingtree == null )
			throw new IOException( "no <" + LABELINGTREE_TAG + "> element found." );

		for ( final Element node : labelingtree.getChildren( LABELINGTREE_NODE_TAG ) ) {
			final int id = XmlHelpers.getInt( node, LABELINGTREE_NODE_ID_TAG );
			final LabelData label = idToLabelMap.get( id );
			if ( label.getLabelingTreeNode() == null )
				labelingPlus.createSegmentAndTreeNode( label );
			final LabelingTreeNode ltn = label.getLabelingTreeNode();
			for ( final int childId : XmlHelpers.getIntArray( node, LABELINGTREE_NODE_CHILDREN_TAG ) ) {
				final LabelData childLabel = idToLabelMap.get( childId );
				if ( childLabel.getLabelingTreeNode() == null )
					labelingPlus.createSegmentAndTreeNode( childLabel );
				ltn.addChild( childLabel.getLabelingTreeNode() );
			}
		}

		for ( final Element forest : labelingtree.getChildren( LABELINGTREE_FOREST_TAG ) ) {
			final int[] rootIds = XmlHelpers.getIntArray( forest, LABELINGTREE_FOREST_ROOTS_TAG );
			final HashSet< LabelingTreeNode > roots = new HashSet<>();
			for ( final int id : rootIds )
				roots.add( idToLabelMap.get( id ).getLabelingTreeNode() );
			labelingPlus.labelingForests.add( new LabelingForest( roots ) );
		}
	}

	private Element toXml(
			final LabelingPlus labelingPlus,
			final File xmlFileDirectory,
			final File indexImgFile ) {
		final LabelingMapping< LabelData > mapping = labelingPlus.getLabeling().getMapping();

		final Element segmentLabeling = new Element( SEGMENTLABELING_TAG );
		segmentLabeling.addContent( XmlHelpers.pathElement(
				BASEPATH_TAG,
				xmlFileDirectory,
				xmlFileDirectory ) );
		segmentLabeling.addContent( XmlHelpers.pathElement(
				INDEXIMG_TAG,
				indexImgFile,
				xmlFileDirectory ) );
		segmentLabeling.addContent( labelIdsElement( LABELS_TAG, mapping.getLabels() ) );

		final Element indexMap = new Element( MAPPING_TAG );
		int i = 0;
		for ( final Set< LabelData > labelSet : new SegmentLabelingSerialisation( mapping ).getLabelSets() ) {
			final Element entry = new Element( MAPPING_ENTRY_TAG );
			entry.addContent( XmlHelpers.intElement( MAPPING_ENTRY_INDEX_TAG, i++ ) );
			entry.addContent( labelIdsElement( MAPPING_ENTRY_LABELS_TAG, labelSet ) );
			indexMap.addContent( entry );
		}
		segmentLabeling.addContent( indexMap );

		segmentLabeling.addContent( labelingTreeElement(
				LABELINGTREE_TAG,
				mapping.getLabels(),
				labelingPlus.getLabelingForests() ) );

		return segmentLabeling;
	}

	private File loadBasePath( final Element root, final File xmlFile ) {
		File xmlFileParentDirectory = xmlFile.getParentFile();
		if ( xmlFileParentDirectory == null ) xmlFileParentDirectory = new File( "." );
		return XmlHelpers.loadPath( root, BASEPATH_TAG, ".", xmlFileParentDirectory );
	}

	private static Element labelIdsElement( final String name, final Set< LabelData > labels ) {
		final int[] ids = new int[ labels.size() ];
		int i = 0;
		for ( final LabelData label : labels )
			ids[ i++ ] = label.getId();
		return XmlHelpers.intArrayElement( name, ids );
	}

	private static TIntObjectMap< LabelData > getIdToLabelMap(
			final Element parent,
			final String name ) {
		final int[] ids = XmlHelpers.getIntArray( parent, name );
		final TIntObjectHashMap< LabelData > map =
				new TIntObjectHashMap<>( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1 );
		for ( final int id : ids )
			map.put( id, new LabelData( id ) );
		return map;
	}

	private static Element labelingTreeElement(
			final String name,
			final Set< LabelData > labels,
			final List< LabelingForest > forests ) {
		final Element labelingTree = new Element( name );
		for ( final LabelData label : labels ) {
			final LabelingTreeNode node = label.getLabelingTreeNode();
			if ( node == null ) continue;

			final int[] childIds = new int[ node.getChildren().size() ];
			int i = 0;
			for ( final LabelingTreeNode child : node.getChildren() )
				childIds[ i++ ] = child.getLabel().getId();

			final Element labelingTreeNode = new Element( LABELINGTREE_NODE_TAG );
			labelingTreeNode.addContent( XmlHelpers.intElement(
					LABELINGTREE_NODE_ID_TAG,
					label.getId() ) );
			labelingTreeNode.addContent( XmlHelpers.intArrayElement(
					LABELINGTREE_NODE_CHILDREN_TAG,
					childIds ) );
			labelingTree.addContent( labelingTreeNode );
		}

		for ( final LabelingForest forest : forests )
		{
			final int[] rootIds = new int[ forest.roots().size() ];
			int i = 0;
			for ( final LabelingTreeNode root : forest.roots() )
				rootIds[ i++ ] = root.getLabel().getId();
			final Element forestNode = new Element( LABELINGTREE_FOREST_TAG );
			forestNode.addContent( XmlHelpers.intArrayElement(
					LABELINGTREE_FOREST_ROOTS_TAG,
					rootIds ) );
			labelingTree.addContent( forestNode );
		}

		return labelingTree;
	}

	/**
	 * IJ opens the int image as float32, and we convert it to a int
	 * {@link ArrayImg}. This is a receipe for disaster but as long as the ints
	 * in the image don't get too big it will work...
	 */
	private static ArrayImg< IntType, IntArray > openIntImageFastButDangerous( final String fn )
	{
		final ImagePlus imp = new ImagePlus( fn );
		final Img<FloatType> wrapped = ImageJFunctions.wrapFloat( imp );
		final long[] dimensions = new long[ wrapped.numDimensions() ];
		wrapped.dimensions( dimensions );
		final ArrayImg< IntType, IntArray > img = ArrayImgs.ints( dimensions );
		final Cursor< FloatType > in = Views.flatIterable( wrapped ).cursor();
		final ArrayCursor< IntType > out = img.cursor();
		while( out.hasNext() )
			out.next().set( ( int ) in.next().get() );
		return img;
	}

	private static class SegmentLabelingSerialisation
			extends
			LabelingMapping.SerialisationAccess< LabelData > {

		public SegmentLabelingSerialisation( final LabelingMapping< LabelData > mapping ) {
			super( mapping );
		}

		@Override
		protected List< Set< LabelData > > getLabelSets() {
			return super.getLabelSets();
		}

		@Override
		protected void setLabelSets( final List< Set< LabelData > > labelSets ) {
			super.setLabelSets( labelSets );
		}
	}
}
