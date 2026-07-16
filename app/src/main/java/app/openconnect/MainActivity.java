/*
 * Adapted from OpenVPN for Android
 * Copyright (c) 2012-2013, Arne Schwabe
 * Copyright (c) 2013, Kevin Cernekee
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * In addition, as a special exception, the copyright holders give
 * permission to link the code of portions of this program with the
 * OpenSSL library.
 */

package app.openconnect;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import app.openconnect.core.OpenConnectManagementThread;
import app.openconnect.core.OpenVpnService;
import app.openconnect.core.VPNConnector;
import app.openconnect.fragments.*;
import app.openconnect.update.GitHubUpdateChecker;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends ToolbarActivity {

	public static final String TAG = "OpenConnect";
	private static final int MENU_CHECK_UPDATES = 40;

	private TabLayout mTabs;

	private ArrayList<TabContainer> mTabList = new ArrayList<TabContainer>();

	private TabContainer mConnectionTab;
	private int mLastTab;
	private boolean mTabsActive;

	private int mConnectionState = OpenConnectManagementThread.STATE_DISCONNECTED;
	private VPNConnector mConn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabbed);
		setupToolbar(R.id.toolbar, getString(R.string.app), false);
		mTabs = (TabLayout)findViewById(R.id.tabs);

		mTabsActive = false;
		if (savedInstanceState != null) {
			mLastTab = savedInstanceState.getInt("active_tab");
		}

		mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				TabContainer tc = (TabContainer)tab.getTag();
				if (tc != null) {
					showTab(tc);
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});

		mTabList.add(new TabContainer(0, R.string.vpn_list_title, new VPNProfileList()));
		mTabList.add(new TabContainer(1, R.string.log, new LogFragment()));
		mTabList.add(new TabContainer(2, R.string.faq, new FaqFragment()));

		mConnectionTab = mTabList.get(0);

		int selectedTab = Math.max(0, Math.min(mLastTab, mTabList.size() - 1));
		for (TabContainer tc : mTabList) {
			mTabs.addTab(tc.tab, false);
		}
		mTabs.selectTab(mTabList.get(selectedTab).tab);
		mTabsActive = true;

		FeedbackFragment.recordUse(this, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putInt("active_tab", mLastTab);
	}

	private void updateUI(OpenVpnService service) {
		int newState = service.getConnectionState();

		service.startActiveDialog(this);

		if (mConnectionState != newState) {
			if (newState == OpenConnectManagementThread.STATE_DISCONNECTED) {
				mConnectionTab.replace(R.string.vpn_list_title, new VPNProfileList());
			} else if (mConnectionState == OpenConnectManagementThread.STATE_DISCONNECTED) {
				mConnectionTab.replace(R.string.status, new StatusFragment());
			}
			mConnectionState = newState;
		}

		invalidateToolbarMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GitHubUpdateChecker.checkAutomatically(this);

		mConn = new VPNConnector(this, true) {
			@Override
			public void onUpdate(OpenVpnService service) {
				updateUI(service);
			}
		};
	}

	@Override
	protected void onPause() {
		mConn.stopActiveDialog();
		mConn.unbind();
		super.onPause();
	}

	@Override
	protected void onCreateToolbarMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_CHECK_UPDATES, Menu.NONE, R.string.check_for_updates)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	}

	@Override
	protected boolean onToolbarMenuItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_CHECK_UPDATES) {
			GitHubUpdateChecker.checkManually(this);
			return true;
		}
		return super.onToolbarMenuItemSelected(item);
	}

	private void showTab(TabContainer tc) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (mTabsActive) {
			if (tc.idx < mLastTab) {
				ft.setCustomAnimations(R.animator.fragment_slide_right_enter,
						R.animator.fragment_slide_right_exit);
			} else if (tc.idx > mLastTab) {
				ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
						R.animator.fragment_slide_left_exit);
			}
		}

		mLastTab = tc.idx;
		for (TabContainer tab : mTabList) {
			tab.mActive = tab == tc;
		}
			ft.replace(R.id.content_frame, tc.mFragment);
			ft.commit();
			getFragmentManager().executePendingTransactions();
			setToolbarMenuFragment(tc.mFragment);
		}

	public void showLogTab() {
		if (mTabs != null && mTabList.size() > 1) {
			mTabs.selectTab(mTabList.get(1).tab);
		}
	}

	public void retryLastConnection() {
		if (mConn != null && mConn.service != null &&
				mConn.service.getReconnectUUID() != null) {
			mConn.service.startReconnectActivity(this);
		}
	}

	protected class TabContainer {
		private Fragment mFragment;
		private boolean mActive;
		public TabLayout.Tab tab;
		public int idx;

		public void replace(int titleResId, Fragment frag) {
			mFragment = frag;
			tab.setText(titleResId);

			if (mActive) {
					getFragmentManager().beginTransaction()
						.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
						.replace(R.id.content_frame, mFragment)
						.commit();
					getFragmentManager().executePendingTransactions();
					setToolbarMenuFragment(mFragment);
				}
			}

		public TabContainer(int idx, int titleResId, Fragment frag) {
			this.idx = idx;
			this.mFragment = frag;
			tab = mTabs.newTab().setText(titleResId);
			tab.setTag(this);
		}
	}
}
