package pt.simov.services;

import pt.simov.interfaces.ServiceAnomaliesInterface;
import pt.simov.interfaces.ServicePoiInterface;
import pt.simov.interfaces.ServiceUserInterface;
import pt.simov.interfaces.ServiceLostAndFoundInterface;

public class ServiceFacade {

    private ServiceUserInterface serviceUserInterface;
    private ServicePoiInterface servicePoiInterface;
	private ServiceLostAndFoundInterface serviceLostAndFoundInterface;
	private ServiceAnomaliesInterface serviceAnomaliesInterface;

    public ServiceFacade(){
        this.serviceUserInterface=new ServiceUserImpl();
        this.servicePoiInterface=new ServicePoiImpl();
        this.serviceLostAndFoundInterface=new ServiceLostAndFoundImpl();
        this.serviceAnomaliesInterface=new ServiceAnomaliesImpl();
    }
    public ServiceUserInterface getServiceUserInterface(){
        return this.serviceUserInterface;
    }
    public ServicePoiInterface getServicePoiInterface(){
        return this.servicePoiInterface;
    }
    public ServiceLostAndFoundInterface getServiceLostAndFoundInterface() {
        return this.serviceLostAndFoundInterface;
    }
    public ServiceAnomaliesInterface getServiceAnomaliesInterface() {
        return this.serviceAnomaliesInterface;
    }


}

