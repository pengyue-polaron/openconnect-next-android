/*
 * Copyright (c) 2026
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package app.openconnect;

import android.app.Activity;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;

public class ToolbarActivity extends Activity {
	protected MaterialToolbar mToolbar;
	private Fragment mToolbarMenuFragment;

	protected void setupToolbar(int toolbarId, CharSequence title, boolean showBack) {
		mToolbar = (MaterialToolbar)findViewById(toolbarId);
		mToolbar.setTitle(title);
		if (showBack) {
			mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
			mToolbar.setNavigationContentDescription(R.string.navigate_up);
			mToolbar.setNavigationOnClickListener(v -> finish());
		}
		mToolbar.setOnMenuItemClickListener(this::onToolbarMenuItemSelected);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if (mToolbar != null) {
			mToolbar.setTitle(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getText(titleId));
	}

	protected void setToolbarMenuFragment(Fragment fragment) {
		mToolbarMenuFragment = fragment;
		invalidateToolbarMenu();
	}

	protected void invalidateToolbarMenu() {
		if (mToolbar == null) {
			return;
		}
		Menu menu = mToolbar.getMenu();
		menu.clear();
		onCreateToolbarMenu(menu);
		if (mToolbarMenuFragment != null) {
			mToolbarMenuFragment.onCreateOptionsMenu(menu, getMenuInflater());
		}
	}

	protected void onCreateToolbarMenu(Menu menu) {
	}

	protected boolean onToolbarMenuItemSelected(MenuItem item) {
		if (mToolbarMenuFragment != null && mToolbarMenuFragment.onOptionsItemSelected(item)) {
			return true;
		}
		return onOptionsItemSelected(item);
	}
}
