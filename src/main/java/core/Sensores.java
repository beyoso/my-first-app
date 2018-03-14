package core;

public class Sensores {

	private int idArduino;
	private int HumedadS;
	private int HumedadA;
	private float Temperatura;
	private int fecha;

	public Sensores() {
		this(0, 0, 0, 0, 0);
	}
	public Sensores(int idArduino, int humedadS, int humedadA, float temperatura, int fecha) {
		super();
		this.idArduino = idArduino;
		this.HumedadS = humedadS;
		this.HumedadA = humedadA;
		this.Temperatura = temperatura;
		this.fecha = fecha;
	};

	public int getIdArduino() {
		return this.idArduino;
	}

	public int getGetHumedadS() {
		return this.HumedadS;
	}

	public int getGetHumedadA() {
		return this.HumedadA;
	}

	public float getGetTemperatura() {
		return this.Temperatura;
	}
	
	public int getFecha() {
		return this.fecha;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + HumedadA;
		result = prime * result + HumedadS;
		result = prime * result + Float.floatToIntBits(Temperatura);
		result = prime * result + fecha;
		result = prime * result + idArduino;
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
		Sensores other = (Sensores) obj;
		if (HumedadA != other.HumedadA)
			return false;
		if (HumedadS != other.HumedadS)
			return false;
		if (Float.floatToIntBits(Temperatura) != Float.floatToIntBits(other.Temperatura))
			return false;
		if (fecha != other.fecha)
			return false;
		if (idArduino != other.idArduino)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sensores [idArduino=" + idArduino + ", HumedadS=" + HumedadS + ", HumedadA=" + HumedadA
				+ ", Temperatura=" + Temperatura + ", fecha=" + fecha + "]";
	}

}
