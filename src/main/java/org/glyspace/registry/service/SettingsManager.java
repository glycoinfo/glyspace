package org.glyspace.registry.service;

public interface SettingsManager {
	public void setDelay (long newDelay);
	public long getDelay();
	
	public void setQuotaPeriod (long newPeriod);
	public long getQuotaPeriod();
}
