function D( FG, dpcm, dpcmAQ, m1, s1, m2, s2 )
    figure('Name','Frequency Generator','NumberTitle','off')
    plot( FG );
    figure('Name','Audio','NumberTitle','off')
    plot( dpcm );
    
    
    figure('Name','DPCM signal','NumberTitle','off')
    hist( dpcm, min(dpcm):max(dpcm) );
    
    figure('Name','DPCM diferences','NumberTitle','off')
    diffdpcm = dpcm' - circshift( dpcm', 1 );
    diffdpcm = diffdpcm(2:size(diffdpcm,1) );
    hist( diffdpcm, min(diffdpcm):max(diffdpcm) );
    
    figure('Name','DPCM AQ signal','NumberTitle','off')
    hist( dpcmAQ, min(dpcmAQ):max(dpcmAQ) );
    
    figure('Name','DPCM AQ diferences','NumberTitle','off')
    diffdpcmaq = dpcmAQ' - circshift( dpcmAQ', 1 );
    diffdpcmaq = diffdpcmaq(2:size(diffdpcmaq,1) );
    hist( diffdpcmaq, min(diffdpcmaq):max(diffdpcmaq) );
    
    figure('Name','DPCM AQ mean 1','NumberTitle','off')
    plot( m1 );
    figure('Name','DPCM AQ std 1','NumberTitle','off')
    plot( s1 );
    
    figure('Name','DPCM AQ mean 2','NumberTitle','off')
    plot( m2 );
    figure('Name','DPCM AQ std 2','NumberTitle','off')
    plot( s2 );
end
