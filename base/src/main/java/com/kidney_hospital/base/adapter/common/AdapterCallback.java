package com.kidney_hospital.base.adapter.common;

import android.os.Bundle;

public interface AdapterCallback {
	
	void adapterstartActivity(Class<?> className, Bundle options);
	void adapterstartActivityForResult(Class<?> className, Bundle options, int requestcode);

	void adapterInfotoActiity(Object data, int what);
}
