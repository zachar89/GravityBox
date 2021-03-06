/*
 * Copyright (C) 2018 Peter Gregus for GravityBox Project (C3C076@xda)
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

package com.ceco.oreo.gravitybox;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.Unhook;
import de.robv.android.xposed.XposedHelpers;

import java.util.ArrayList;
import java.util.List;

import com.ceco.oreo.gravitybox.managers.StatusBarIconManager;
import com.ceco.oreo.gravitybox.managers.SysUiManagers;
import com.ceco.oreo.gravitybox.managers.StatusBarIconManager.ColorInfo;
import com.ceco.oreo.gravitybox.managers.StatusBarIconManager.IconManagerListener;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

public class StatusbarBattery implements IconManagerListener {
    private static final String TAG = "GB:StatusbarBattery";
    private static final boolean DEBUG = false;

    private View mBattery;
    private int mDefaultColor;
    private int mDefaultFrameColor;
    private int mFrameAlpha;
    private int mDefaultChargeColor;
    private Drawable mDrawable;
    private List<Unhook> mHooks = new ArrayList<>();

    public StatusbarBattery(View batteryView) {
        mBattery = batteryView;
        backupOriginalColors();
        createHooks();
        if (SysUiManagers.IconManager != null && !Utils.isParanoidRom()) {
            SysUiManagers.IconManager.registerListener(this);
        }
    }

    public void destroy() {
        if (SysUiManagers.IconManager != null) {
            SysUiManagers.IconManager.unregisterListener(this);
        }
        for (Unhook hook : mHooks) {
            hook.unhook();
        }
        mHooks.clear();
        mHooks = null;
        mDrawable = null;
        mBattery = null;
    }

    private void backupOriginalColors() {
        if (Utils.isParanoidRom())
            return;

        try {
            final int[] colors = (int[]) XposedHelpers.getObjectField(getDrawable(), "mColors");
            mDefaultColor = colors[colors.length-1];
            final Paint framePaint = (Paint) XposedHelpers.getObjectField(getDrawable(), "mFramePaint");
            mDefaultFrameColor = framePaint.getColor();
            mFrameAlpha = framePaint.getAlpha();
            mDefaultChargeColor = XposedHelpers.getIntField(getDrawable(), "mChargeColor");
        } catch (Throwable t) {
            GravityBox.log(TAG, "Error backing up original colors: ", t);
        }
    }

    private Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                mDrawable = (Drawable) XposedHelpers.getObjectField(mBattery, "mDrawable");
            } catch (Throwable t) {
                GravityBox.log(TAG, t);
            }
        }
        return mDrawable;
    }

    private void createHooks() {
        if (!Utils.isXperiaDevice() && !Utils.isParanoidRom() && getDrawable() != null) {
            try {
                mHooks.add(XposedHelpers.findAndHookMethod(getDrawable().getClass(), "setColorFilter",
                        ColorFilter.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (SysUiManagers.IconManager != null &&
                                SysUiManagers.IconManager.isColoringEnabled()) {
                            int color = SysUiManagers.IconManager.getIconColor();
                            setColors(color, color, color);
                        }
                    }
                }));
            } catch (Throwable t) {
                if (!Utils.isOxygenOsRom()) {
                    GravityBox.log(TAG, "Error hooking setColorFilter(): ", t);
                }
            }
            try {
                mHooks.add(XposedHelpers.findAndHookMethod(getDrawable().getClass(), "setColors",
                        int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (SysUiManagers.IconManager != null &&
                                SysUiManagers.IconManager.isColoringEnabled()) {
                            int color = SysUiManagers.IconManager.getIconColor();
                            setColors(color, color, color);
                        }
                    }
                }));
            } catch (Throwable t) {
                GravityBox.log(TAG, "Error hooking setColors(): ", t);
            }
        }
    }

    public void setVisibility(int visibility) {
        mBattery.setVisibility(visibility);
    }

    private void setColors(int mainColor, int frameColor, int chargeColor) {
        if (mBattery != null && getDrawable() != null) {
            try {
                final int[] colors = (int[]) XposedHelpers.getObjectField(getDrawable(), "mColors");
                colors[colors.length-1] = mainColor;
                final Paint framePaint = (Paint) XposedHelpers.getObjectField(getDrawable(), "mFramePaint");
                framePaint.setColor(frameColor);
                framePaint.setAlpha(mFrameAlpha);
                XposedHelpers.setIntField(getDrawable(), "mChargeColor", chargeColor);
                XposedHelpers.setIntField(getDrawable(), "mIconTint", mainColor);
            } catch (Throwable t) {
                GravityBox.log(TAG, "Error setting colors: ", t);
            }
        }
    }

    public void setShowPercentage(boolean showPercentage) {
        if (mBattery != null && getDrawable() != null) {
            try {
                XposedHelpers.setBooleanField(getDrawable(), "mShowPercent", showPercentage);
                mBattery.invalidate();
            } catch (Throwable t) {
                GravityBox.log(TAG, "Error setting percentage: ", t);
            }
        }
    }

    @Override
    public void onIconManagerStatusChanged(int flags, ColorInfo colorInfo) {
        if ((flags & StatusBarIconManager.FLAG_ICON_COLOR_CHANGED) != 0) {
            if (colorInfo.coloringEnabled) {
                setColors(colorInfo.iconColor[0], colorInfo.iconColor[0], colorInfo.iconColor[0]);
            } else {
                setColors(mDefaultColor, mDefaultFrameColor, mDefaultChargeColor);
            }
            mBattery.invalidate();
        }
    }
}
