function [m, s] = C1( e )
    m = mean( e );
    s = std( e ) ^ 2;
end