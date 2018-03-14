package core;

public class Arduino {
	private Integer nombre;
	private Sensores sensores;

	public Arduino() {
		this(null,null);
	};
	public Arduino(Integer nombre, Sensores s) {
		super();
		this.nombre = nombre;
		this.sensores = s;
	}
	
	public Integer getNombre() {
		return this.nombre;
	}
	public Sensores getSensores() {
		return this.sensores;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((sensores == null) ? 0 : sensores.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arduino other = (Arduino) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (sensores == null) {
			if (other.sensores != null)
				return false;
		} else if (!sensores.equals(other.sensores))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Arduino [nombre=" + nombre + ", sensores=" + sensores + "]";
	}
	
}
