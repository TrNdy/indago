/**
 *
 */
package com.indago.io;

import java.io.IOException;

import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.io.IOService;

import com.indago.IndagoLog;

import io.scif.codec.CodecService;
import io.scif.formats.qt.QTJavaService;
import io.scif.formats.tiff.TiffService;
import io.scif.img.ImgUtilityService;
import io.scif.services.DatasetIOService;
import io.scif.services.JAIIIOService;
import io.scif.services.LocationService;
import io.scif.services.TranslatorService;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Since loading and saving is still a hard thing to do right and the way to do
 * it using IJ(1/2)/Fiji changes constantly (plus there are shitloads of bugs),
 * I need to make this additional layer of abstraction. Tadaaa!
 *
 * @author jug
 */
public class ImageSaver {

	// Needed for current workaround... sorry!
	public static Context context = new Context( OpService.class, OpMatchingService.class,
			IOService.class, DatasetIOService.class, LocationService.class,
			DatasetService.class, ImgUtilityService.class, StatusService.class,
			TranslatorService.class, QTJavaService.class, TiffService.class,
			CodecService.class, JAIIIOService.class );

	/**
	 * @param filename
	 * @param rai
	 * @param context
	 *            Needed for current workaround... sorry!
	 */
	public static < T extends RealType< T > > void saveAsTiff(
			final String filename,
			final RandomAccessibleInterval< T > rai ) {

		// What I would like to do but brewaks currently as soon as you are within Fiji/Imagej2 (e.g. as Command)
//		IO.saveImg( filename, img );

		// The only workaround I know works at the moment (2016-08-05)
		if ( context == null ) IndagoLog.log.error( "Static field 'context' was not set before using ImageSaver..." );
		final DatasetService datasetService = context.getService( DatasetService.class );
		final Dataset dataset = datasetService.create( rai );
		final DatasetIOService service = context.getService( DatasetIOService.class );
		try {
			service.save( dataset, filename );
		} catch ( final IOException exc ) {
			exc.printStackTrace();
		}
	}
}
