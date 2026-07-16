/*
 * Copyright (c) 2026 OpenConnect Next contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package app.openconnect.core;

import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import app.openconnect.MainActivity;
import app.openconnect.R;
import app.openconnect.api.GrantPermissionsActivity;

@TargetApi(Build.VERSION_CODES.N)
@SuppressLint({"StartActivityAndCollapseDeprecated", "UseRequiresApi"})
public class QuickSettingsTileService extends TileService {

	private OpenVpnService mService;
	private boolean mBound;
	private boolean mBinding;
	private boolean mListening;
	private boolean mPendingClick;

	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mBinding = false;
			mBound = true;
			mService = ((OpenVpnService.LocalBinder)binder).getService();
			updateTile();

			if (mPendingClick) {
				mPendingClick = false;
				toggleConnection();
			}
			if (!mListening) {
				unbindVpnService();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			mBound = false;
			mBinding = false;
			updateTile();
		}
	};

	@Override
	public void onStartListening() {
		super.onStartListening();
		mListening = true;
		bindVpnService();
	}

	@Override
	public void onStopListening() {
		mListening = false;
		if (!mPendingClick) {
			unbindVpnService();
		}
		super.onStopListening();
	}

	@Override
	public void onClick() {
		super.onClick();
		if (mService == null) {
			mPendingClick = true;
			bindVpnService();
			return;
		}
		toggleConnection();
	}

	@Override
	public void onDestroy() {
		mPendingClick = false;
		unbindVpnService();
		super.onDestroy();
	}

	private void bindVpnService() {
		if (mBound || mBinding) {
			return;
		}

		Intent intent = new Intent(this, OpenVpnService.class);
		intent.setAction(OpenVpnService.START_SERVICE);
		mBinding = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		if (!mBinding) {
			mPendingClick = false;
			updateTile();
		}
	}

	private void unbindVpnService() {
		if (!mBound && !mBinding) {
			return;
		}

		try {
			unbindService(mConnection);
		} catch (IllegalArgumentException ignored) {
			// The system may disconnect a tile service before this callback runs.
		}
		mService = null;
		mBound = false;
		mBinding = false;
	}

	private void toggleConnection() {
		if (mService.getConnectionState() != OpenConnectManagementThread.STATE_DISCONNECTED) {
			String profileName = mService.getReconnectName();
			mService.stopVPN();
			showDisconnectedTile(profileName);
			return;
		}

		String uuid = mService.getReconnectUUID();
		Intent intent;
		if (uuid == null) {
			intent = new Intent(this, MainActivity.class);
		} else {
			intent = new Intent(this, GrantPermissionsActivity.class);
			intent.putExtra(getPackageName() + GrantPermissionsActivity.EXTRA_UUID, uuid);
			intent.setAction(Intent.ACTION_MAIN);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityAndCollapseCompat(intent);
	}

	@SuppressWarnings("deprecation")
	private void startActivityAndCollapseCompat(Intent intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			startActivityAndCollapse(pendingIntent);
		} else {
			startActivityAndCollapse(intent);
		}
	}

	private void updateTile() {
		Tile tile = getQsTile();
		if (tile == null) {
			return;
		}

		tile.setLabel(getString(R.string.quick_settings_label));
		if (mService == null) {
			tile.setState(Tile.STATE_INACTIVE);
			setSubtitle(tile, getString(R.string.quick_settings_choose_profile));
		} else {
			boolean disconnected = mService.getConnectionState() ==
					OpenConnectManagementThread.STATE_DISCONNECTED;
			String profileName = mService.getReconnectName();
			tile.setState(disconnected ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
			if (profileName == null) {
				setSubtitle(tile, getString(R.string.quick_settings_choose_profile));
			} else if (disconnected) {
				setSubtitle(tile, getString(R.string.quick_settings_connect_to, profileName));
			} else {
				setSubtitle(tile, getString(R.string.quick_settings_connected_to, profileName));
			}
		}
		tile.updateTile();
	}

	private void showDisconnectedTile(String profileName) {
		Tile tile = getQsTile();
		if (tile == null) {
			return;
		}

		tile.setLabel(getString(R.string.quick_settings_label));
		tile.setState(Tile.STATE_INACTIVE);
		if (profileName == null) {
			setSubtitle(tile, getString(R.string.quick_settings_choose_profile));
		} else {
			setSubtitle(tile, getString(R.string.quick_settings_connect_to, profileName));
		}
		tile.updateTile();
	}

	private void setSubtitle(Tile tile, String subtitle) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			tile.setSubtitle(subtitle);
		}
	}
}
