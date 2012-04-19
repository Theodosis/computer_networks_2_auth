function C2( FG )
    FG = FG( 10000:size(FG,2) );
    FG = FG - mean( FG );
    L = size( FG, 2 );
    NFFT = 2^nextpow2( L );
    FS = 8000;
    Y = fft( FG, NFFT ) / L;
    f = FS / 2 * linspace( 0, 1, NFFT/2+1 );
    plot( f, 2 * abs( Y ( 1:NFFT/2+1 ) ) );
end
