package com.xtremelabs.robolectric.shadows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.internal.RealObject;
import com.xtremelabs.robolectric.tester.android.util.TestFragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;

@Implements(FragmentActivity.class)
public class ShadowFragmentActivity extends ShadowActivity {
    @RealObject
    FragmentActivity realObject;

    private FragmentManager fragmentManager;
    public static final String FRAGMENTS_TAG = "android:fragments";

    public void __constructor__() {
        fragmentManager = new TestFragmentManager(realObject);
    }

    @Implementation
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle != null && bundle.containsKey(FRAGMENTS_TAG)) {
            Object[] fragments = (Object[]) bundle.getSerializable(FRAGMENTS_TAG);

            for (Object o : fragments) {
                SerializedFragmentState fragmentState = (SerializedFragmentState) o;

                try {
                    Fragment fragment = fragmentState.fragmentClass.newInstance();
                    shadowOf(fragment).setSavedInstanceState(bundle);
                    fragmentManager.beginTransaction()
                            .add(fragmentState.containerId, fragment, fragmentState.tag)
                            .commit();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Implementation
    public FragmentManager getSupportFragmentManager() {
        return fragmentManager;
    }

    @Implementation
    public void onSaveInstanceState(Bundle outState) {
        // We cannot figure out how to pass the RobolectricWiring test without doing this incredibly
        // terrible looking hack.  I am very sorry.
        List<SerializedFragmentState> fragmentStates = new ArrayList<SerializedFragmentState>();

        for (Map.Entry<Integer, Fragment> entry : ((TestFragmentManager) fragmentManager).getFragments().entrySet()) {
            fragmentStates.add(new SerializedFragmentState(entry.getKey(), entry.getValue()));
        }

        outState.putSerializable(FRAGMENTS_TAG, fragmentStates.toArray());
    }
}