package transport.core;

import java.io.Serializable;

public interface Suspendable extends Serializable {
    void suspendre();
    void reactiver();
    boolean estSuspendu();
    String getEtat();
}
