package powercrystals.minefactoryreloaded.gui.control;

public interface IListBoxElement
{
	public int getHeight();
	
	public Object getValue();
	
	public void draw(ListBox listBox, int x, int y, int backColor, int textColor);
}
