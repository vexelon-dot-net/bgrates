package net.vexelon.bgrates;

public class ConvertRow {
	
	enum RowType {
		RowBGN,
		RowOthers
	};	
	
	private static int lastId = 0;
	
	private int rowId;
	private int spinnerId;
	private int editTextId;
	private RowType rowType;
	
	public ConvertRow(int spinnerId, int editTextId, RowType rowType) {
		this.spinnerId = spinnerId;
		this.editTextId = editTextId;
		this.rowId = ++ConvertRow.lastId;
		this.rowType = rowType;
	}

	public int getRowId() {
		return rowId;
	}

	public int getSpinnerId() {
		return spinnerId;
	}

	public int getEditTextId() {
		return editTextId;
	}	
	
	public RowType getRowType() {
		return rowType;
	}
}
