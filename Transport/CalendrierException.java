package Transport;

public class CalendrierException {
	private String service_id;
	private int date_exception ;
	private int type_exception ;


	public CalendrierException(String service_id) {
		this.service_id = service_id ;
	}


	public CalendrierException(String service_id, int date_exception, int type_exception) {
		this.service_id = service_id;
		this.date_exception = date_exception;
		this.type_exception = type_exception;
	}


	public String getService_id() {
		return service_id;
	}


	public int getDate_exception() {
		return date_exception;
	}


	public int getType_exception() {
		return type_exception;
	}

	public String getStringType_exception() {
		switch(type_exception) {
		case 1 :
			return "le service a été ajouté pour la date spécifiée ("+date_exception+")";
		default :
			return "le service a été supprimé pour la date spécifiée ("+date_exception+")";
		}
	}
	public void setDate_exception(int date_exception) {
		this.date_exception = date_exception;
	}


	public void setType_exception(int type_exception) {
		this.type_exception = type_exception;
	}

	

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof CalendrierException))
			return false;
		CalendrierException o = (CalendrierException) obj;
		if(!this.service_id.equals( o.service_id ))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.service_id.hashCode();
	}



}