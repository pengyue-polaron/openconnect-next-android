/*
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

import app.openconnect.core.ProfileManager;
import app.openconnect.fragments.ConnectionEditorFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ConnectionEditorActivity extends ToolbarActivity {

    private String mName = "";
    private String mUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		setupToolbar(R.id.toolbar, getString(R.string.app), true);

        ConnectionEditorFragment frag = new ConnectionEditorFragment();
        mUUID = getIntent().getStringExtra(getPackageName() + ".profileUUID");
        Bundle args = new Bundle();
        args.putString("profileUUID", mUUID);
        frag.setArguments(args);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, frag)
                .commit();
		invalidateToolbarMenu();
    }

	@Override
	protected void onCreateToolbarMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.vpnpreferences_menu, menu);
	}

	@Override
	protected boolean onToolbarMenuItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.remove_vpn) {
			askProfileRemoval();
			return true;
		}
		return super.onToolbarMenuItemSelected(item);
	}

	public void setProfileName(String name) {
		mName = name;
    	setTitle(getString(R.string.edit_profile_title, mName));
	}

	private void askProfileRemoval() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.confirm_deletion);
		dialog.setMessage(getString(R.string.remove_vpn_query, mName));

		dialog.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ProfileManager.delete(mUUID);
				finish();
			}
		});
		dialog.setNegativeButton(android.R.string.no,null);
		dialog.create().show();
	}
}
