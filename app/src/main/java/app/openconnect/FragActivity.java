/*
 * Copyright (c) 2014, Kevin Cernekee
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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public class FragActivity extends ToolbarActivity {

	public static final String TAG = "OpenConnect";

	public static final String EXTRA_FRAGMENT_NAME = "app.openconnect.fragment_name";

	public static final String FRAGMENT_PREFIX = "app.openconnect.fragments.";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		String fragName = getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
		setupToolbar(R.id.toolbar, getTitleForFragment(fragName), true);

		if(savedInstanceState == null) {
			try {
					Fragment frag = (Fragment)Class.forName(FRAGMENT_PREFIX + fragName).newInstance();
					getFragmentManager().beginTransaction().add(R.id.content_frame, frag).commit();
					getFragmentManager().executePendingTransactions();
					setToolbarMenuFragment(frag);
				} catch (Exception e) {
				Log.e(TAG, "unable to create fragment", e);
				finish();
			}
		} else {
			Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
			if (frag != null) {
				setToolbarMenuFragment(frag);
			}
		}
    }

	private CharSequence getTitleForFragment(String fragName) {
		if ("GeneralSettings".equals(fragName)) {
			return getString(R.string.generalsettings);
		} else if ("TokenParentFragment".equals(fragName)) {
			return getString(R.string.securid_info);
		} else if ("AboutFragment".equals(fragName)) {
			return getString(R.string.about_openconnect);
		} else if ("FeedbackFragment".equals(fragName)) {
			return getString(R.string.report_problem);
		} else if ("SendDumpFragment".equals(fragName)) {
			return getString(R.string.send_minidump);
		}
		return getString(R.string.app);
	}

}
