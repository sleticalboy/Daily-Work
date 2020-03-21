package com.sleticalboy.util;

import android.content.Context;
import android.util.Log;

/**
 * Created on 18-4-2.
 *
 * @author leebin
 * @description
 */
public class MResource {
    public MResource() {
    }

    public static int getIdByName(Context context, String className, String name) {
        String packageName = context.getPackageName();
        Log.d("MResource", packageName);
        Class r;
        int id = 0;

        try {
            r = Class.forName(packageName + ".R");
            Log.d("MResource", r.getName());
            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; ++i) {
                Log.d("MResource", classes[i].getName());
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }

            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
                Log.d("MResource", desireClass.getName());
            } else {
                desireClass = Class.forName(packageName + ".R$" + className);
                if (desireClass != null) {
                    id = desireClass.getField(name).getInt(desireClass);
                    Log.d("MResource", desireClass.getName());
                }
            }
        } catch (ClassNotFoundException var9) {
            var9.printStackTrace();
            Log.e("AEYE", "getIdByName ClassNotFoundException " + var9.toString());
        } catch (IllegalArgumentException var10) {
            var10.printStackTrace();
            Log.e("AEYE", "getIdByName IllegalArgumentException " + var10.toString());
        } catch (SecurityException var11) {
            var11.printStackTrace();
            Log.e("AEYE", "getIdByName SecurityException " + var11.toString());
        } catch (IllegalAccessException var12) {
            var12.printStackTrace();
            Log.e("AEYE", "getIdByName IllegalAccessException " + var12.toString());
        } catch (NoSuchFieldException var13) {
            var13.printStackTrace();
            Log.e("AEYE", "getIdByName NoSuchFieldException " + var13.toString());
        }

        return id;
    }
}
