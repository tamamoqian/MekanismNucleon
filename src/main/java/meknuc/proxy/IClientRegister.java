package meknuc.proxy;

import java.util.ArrayList;
import java.util.List;

public interface IClientRegister {

    List<IClientRegister> CLIENT_REGISTRARS = new ArrayList<>();

    void registerClient();

    static void addClientRegister(IClientRegister register) {
        CLIENT_REGISTRARS.add(register);
    }

    static void registerAllClients() {
        for (IClientRegister register : CLIENT_REGISTRARS) {
            register.registerClient();
        }
    }
}
