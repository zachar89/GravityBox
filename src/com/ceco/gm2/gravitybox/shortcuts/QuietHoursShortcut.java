/*
 * Copyright (C) 2016 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceco.gm2.gravitybox.shortcuts;

import java.util.ArrayList;
import java.util.List;

import com.ceco.gm2.gravitybox.ModHwKeys;
import com.ceco.gm2.gravitybox.R;
import com.ceco.gm2.gravitybox.adapters.IIconListAdapterItem;
import com.ceco.gm2.gravitybox.ledcontrol.QuietHours;
import com.ceco.gm2.gravitybox.ledcontrol.QuietHoursActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class QuietHoursShortcut extends AMultiShortcut {
    protected static final String ACTION =  ModHwKeys.ACTION_TOGGLE_QUIET_HOURS;

    public QuietHoursShortcut(Context context) {
        super(context);
    }

    @Override
    public String getText() {
        return mContext.getString(R.string.lc_quiet_hours);
    }

    @Override
    public Drawable getIconLeft() {
        return mResources.getDrawable(R.drawable.shortcut_quiet_hours_auto);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    protected String getShortcutName() {
        return getText();
    }

    @Override
    protected List<IIconListAdapterItem> getShortcutList() {
        final List<IIconListAdapterItem> list = new ArrayList<IIconListAdapterItem>();
        list.add(new ShortcutItem(mContext, R.string.shortcut_quiet_hours_toggle, 
                R.drawable.shortcut_quiet_hours, null));
        list.add(new ShortcutItem(mContext, R.string.quiet_hours_on, 
                R.drawable.shortcut_quiet_hours_enable, new ExtraDelegate() {
                    @Override
                    public void addExtraTo(Intent intent) {
                        intent.putExtra(QuietHoursActivity.EXTRA_QH_MODE,
                                QuietHours.Mode.ON.toString());
                    }
                }));
        list.add(new ShortcutItem(mContext, R.string.quiet_hours_off, 
                R.drawable.shortcut_quiet_hours_disable, new ExtraDelegate() {
                    @Override
                    public void addExtraTo(Intent intent) {
                        intent.putExtra(QuietHoursActivity.EXTRA_QH_MODE,
                                QuietHours.Mode.OFF.toString());
                    }
                }));
        list.add(new ShortcutItem(mContext, R.string.quiet_hours_auto, 
                R.drawable.shortcut_quiet_hours_auto, new ExtraDelegate() {
                    @Override
                    public void addExtraTo(Intent intent) {
                        intent.putExtra(QuietHoursActivity.EXTRA_QH_MODE,
                                QuietHours.Mode.AUTO.toString());
                    }
                }));

        return list;
    }

    public static void launchAction(final Context context, Intent intent) {
        Intent launchIntent = new Intent(ACTION);
        if (intent.hasExtra(QuietHoursActivity.EXTRA_QH_MODE)) {
            launchIntent.putExtra(QuietHoursActivity.EXTRA_QH_MODE,
                    intent.getStringExtra(QuietHoursActivity.EXTRA_QH_MODE));
        }
        context.sendBroadcast(launchIntent);
    }
}
