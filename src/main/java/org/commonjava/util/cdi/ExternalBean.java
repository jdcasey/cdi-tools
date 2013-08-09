package org.commonjava.util.cdi;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public interface ExternalBean<T>
    extends Bean<T>
{

    @Override
    int hashCode();

    @Override
    boolean equals( Object other );

    void setBeanManager( BeanManager bm );

    T getInstance();

}
