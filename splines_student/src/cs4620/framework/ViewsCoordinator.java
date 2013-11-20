package cs4620.framework;

public interface ViewsCoordinator
{
	void setViewUpdated(int viewId);
	void resetUpdatedStatus();
	boolean checkAllViewsUpdated();
}
