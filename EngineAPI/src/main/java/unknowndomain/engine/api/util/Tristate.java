package unknowndomain.engine.api.util;

public enum Tristate {

	TRUE(true) {
		@Override
		public Tristate and(Tristate other) {
			return other == TRUE || other == UNDEFINED ? TRUE : FALSE;
		}

		@Override
		public Tristate or(Tristate other) {
			return TRUE;
		}
	},
	FALSE(false) {
		@Override
		public Tristate and(Tristate other) {
			return FALSE;
		}

		@Override
		public Tristate or(Tristate other) {
			return other == TRUE ? TRUE : FALSE;
		}
	},
	UNDEFINED(false) {
		@Override
		public Tristate and(Tristate other) {
			return other;
		}

		@Override
		public Tristate or(Tristate other) {
			return other;
		}
	};

	private final boolean value;

	Tristate(boolean value) {
		this.value = value;
	}

	public static Tristate fromBoolean(boolean value) {
		return value ? TRUE : FALSE;
	}

	public abstract Tristate and(Tristate other);

	public abstract Tristate or(Tristate other);

	public boolean asBoolean() {
		return this.value;
	}
}
