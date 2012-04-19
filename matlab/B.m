function B( e, e0, t, t0 )
    figure('Name','Echo 8 min','NumberTitle','off')
    plot( linspace( 0, 8*60, size(e,1) ), e );
    axis([0,8*60,0,max(e)]);
    
    figure('Name','echo without delay','NumberTitle','off')
    plot( linspace( 0, 4*60, size(e0,1) ), e0 );
    axis([0,4*60,0,max(e0)]);

    figure('Name','throughput','NumberTitle','off')
    plot( linspace( 0, 4*60, size(t,1) ), t );
    axis([0,4*60,0,max(t)]);

    figure('Name','throughput without delay','NumberTitle','off')
    plot( linspace( 0, 4*60, size(t0,1) ), t0 );
    axis([0,4*60,0,max(t0)]);

    
    figure('Name','propability echo','NumberTitle','off')
    hist(e,min(e):max(e));
    
    figure('Name','propability echo without delay','NumberTitle','off')
    hist(e0,min(e0):max(e0));

    figure('Name','propability throughput','NumberTitle','off')
    hist(t,min(t):max(t));
    
    figure('Name','propability throuput without delay','NumberTitle','off')
    hist(t0,min(t0):max(t0));
end