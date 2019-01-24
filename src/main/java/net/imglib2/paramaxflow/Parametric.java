package net.imglib2.paramaxflow;


public class Parametric
{
	static
	{
		// Version number is added for loading the proper native library
		NarHelper
				.loadLibrary( Parametric.class, "net.imglib2", "paramaxflow-jni", "1.0.0-SNAPSHOT" );
	}

	private final long ptr_this;

//	private boolean isFinalized = false;

	public Parametric( final int nodeNumMax, final int edgeNumMax )
	{
		ptr_this = constructor( nodeNumMax, edgeNumMax );
	}

	public long AddNode()
	{
		return AddNode( ptr_this, 1 );
	}

	public long AddNode( final int num )
	{
		return AddNode( ptr_this, num );
	}

	public void AddUnaryTerm( final int i, final double A, final double B )
	{
		AddUnaryTerm( ptr_this, i, A, B );
	}

	public void AddPairwiseTerm( final long i, final long j, final double E00, final double E01, final double E10, final double E11 )
	{
		AddPairwiseTerm( ptr_this, i, j, E00, E01, E10, E11 );
	}

	public long Solve( final double lambdaMin, final double lambdaMax )
	{
		return Solve( ptr_this, lambdaMin, lambdaMax );
	}

	public Region GetFirstRegion()
	{
		return new Region( GetFirstRegion( ptr_this ) );
	}

	public Region GetLastRegion()
	{
		return new Region( GetLastRegion( ptr_this ) );
	}

	public Region GetNextRegion( final Region r )
	{
		return new Region( GetNextRegion( ptr_this, r.ptr_this ) );
	}

	public Region GetPrevRegion( final Region r )
	{
		return new Region( GetPrevRegion( ptr_this, r.ptr_this ) );
	}

	public double GetRegionLambda( final Region r )
	{
		return GetRegionLambda( ptr_this, r.ptr_this );
	}

	public Region GetRegion( final long i )
	{
		return new Region( GetRegion( ptr_this, i ) );
	}

	public long GetRegionCount( final Region r )
	{
		return GetRegionCount( ptr_this, r.ptr_this );
	}

	@Override
	protected void finalize() throws Throwable
	{
		destructor( ptr_this );
		super.finalize();
	}

	private static native long constructor( final int nodeNumMax, final int edgeNumMax );

	private static native void destructor( final long ptr_this );

	private static native long AddNode( final long ptr_this, final int num );

	private static native void AddUnaryTerm( final long ptr_this, final long i, final double A, final double B );

	private static native void AddPairwiseTerm( final long ptr_this, final long i, long j, final double E00, final double E01, final double E10, final double E11 );

	private static native long Solve( final long ptr_this, final double lambdaMin, final double lambdaMax );

	private static native long GetRegionCount( final long ptr_this, final long ptr_r );

	private static native long GetRegion( final long ptr_this, final long i );

	private static native long GetFirstRegion( final long ptr_this );

	private static native long GetLastRegion( final long ptr_this );

	private static native long GetNextRegion( final long ptr_this, final long ptr_r );

	private static native long GetPrevRegion( final long ptr_this, final long ptr_r );

	private static native double GetRegionLambda( final long ptr_this, final long ptr_r );

}
