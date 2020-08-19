package com.sleticalboy.dailywork.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created on 20-5-4.
 *
 * @author BenLee binli@grandstream.cn
 */
public final class BtUtils {

    private BtUtils() {
    }

    // public static CharSequence errorReason(Context context, String name, int reason) {
    //     int errorMsg;
    //     switch(reason) {
    //         case BluetoothDevice.UNBOND_REASON_AUTH_FAILED:
    //             errorMsg = R.string.bluetooth_pairing_pin_error_message;
    //             break;
    //         case BluetoothDevice.UNBOND_REASON_AUTH_REJECTED:
    //             errorMsg = R.string.bluetooth_pairing_rejected_error_message;
    //             break;
    //         case BluetoothDevice.UNBOND_REASON_REMOTE_DEVICE_DOWN:
    //             errorMsg = R.string.bluetooth_pairing_device_down_error_message;
    //             break;
    //         case BluetoothDevice.UNBOND_REASON_DISCOVERY_IN_PROGRESS:
    //         case BluetoothDevice.UNBOND_REASON_AUTH_TIMEOUT:
    //         case BluetoothDevice.UNBOND_REASON_REPEATED_ATTEMPTS:
    //         case BluetoothDevice.UNBOND_REASON_REMOTE_AUTH_CANCELED:
    //             errorMsg = R.string.bluetooth_pairing_error_message;
    //             break;
    //         case BluetoothDevice.UNBOND_REASON_REMOVED:
    //             return "9 Existing bond was explicitly revoked";
    //         case BluetoothDevice.UNBOND_REASON_AUTH_CANCELED:
    //             return "3 Bonding process was cancelled.";
    //         default:
    //             return "No reason for: " + reason;
    //     }
    //     return reason + " " + context.getResources().getString(errorMsg, name);
    // }

    public static String bleTypeToString(int type) {
        if (type == BluetoothDevice.DEVICE_TYPE_DUAL) {
            return "Dual Mode - BR/EDR/LE(3)";
        } else if (type == BluetoothDevice.DEVICE_TYPE_LE) {
            return "Low Energy - LE-only(2)";
        } else if (type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
            return "Classic - BR/EDR devices(1)";
        }
        return "Unknown(0)";
    }

    public static String profileStateToString(int state) {
        if (state == BluetoothProfile.STATE_DISCONNECTED) {
            return "0 DISCONNECTED";
        } else if (state == BluetoothProfile.STATE_CONNECTING) {
            return "1 CONNECTING";
        } else if (state == BluetoothProfile.STATE_CONNECTED) {
            return "2 CONNECTED";
        } else if (state == BluetoothProfile.STATE_DISCONNECTING) {
            return "3 DISCONNECTING";
        } else {
            return "Unknown state " + state;
        }
    }

    public static String gattStatusToString(int status) {
        // GATT_ERROR 0x85 133
        // 任何不惧名字的错误都出现这个错误码，出现了就认怂吧，重新连接吧。
        // GATT_CONN_TIMEOUT  0x08 8
        // 连接超时，大多数情况是设备离开可连接范围，然后手机端连接超时断开返回此错误码。
        // GATT_CONN_TERMINATE_PEER_USER 0x13 19 
        // 连接被对端设备终止，直白点就是手机去连接外围设备，外围设备任性不让连接执行了断开。
        // GATT_CONN_TERMINATE_LOCAL_HOST 0x16 22 
        // 连接被本地主机终止，可以解释为手机连接外围设备，但是连接过程中出现一些比如鉴权等问题，无法继续保持连接，主动执行了断开操作。
        // GATT_CONN_FAIL_ESTABLISH 03E 62  连接建立失败。
        switch (status) {
            case 0:
                return "0(Gatt success)";
            case 1:
                return "1(Gatt connect l2c failure: invalid handle)";
            case 2:
                return "2(Gatt read not permit)";
            case 3:
                return "3(Gatt write not permit)";
            case 4:
                return "4(Gatt invalid pdu)";
            case 5:
                return "5(Gatt insuf authentication)";
            case 6:
                return "6(Gatt req not supported)";
            case 7:
                return "7(Gatt invalid offset)";
            case 8:
                return "8(Gatt connect timeout: discover primary service timeout)";
            case 9:
                return "9(Gatt prepare q full)";
            case 10:
                return "10(Gatt not found)";
            case 11:
                return "11(Gatt not long)";
            case 12:
                return "12(Gatt insuf key size)";
            case 13:
                return "13(Gatt invalid attr len)";
            case 14:
                return "14(Gatt err unlikely)";
            case 15:
                return "15(Gatt insuf encryption)";
            case 16:
                return "16(Gatt unsupport grp type)";
            case 17:
                return "17(Gatt insuf resource)";
            case 19:
                return "19(Gatt connect terminate peer user)";
            case 22:
                return "22(Gatt connect terminate local host)";
            case 34:
                return "34(Gatt connect lmp timeout)";
            case 62:
                return "62(Gatt connect fail establish)";
            case 133:
                return "133(Gatt error)";
            case 256:
                return "256(Gatt connect cancel)";
            case 257:
                return "257(Too many open connections)";
            default:
                return "Unknown status " + status;
        }
    }

    public static boolean createBond(@NonNull BluetoothDevice bt) {
        if (bt.getBondState() == BluetoothDevice.BOND_NONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Object ret = invoke(bt, bt.getClass(), "createBond", BluetoothDevice.TRANSPORT_LE);
                return ret instanceof Boolean && ((Boolean) ret);
            } else {
                return bt.createBond();
            }
        }
        return false;
    }

    public static boolean removeBond(@NonNull BluetoothDevice bt) {
        final int state = bt.getBondState();
        if (state == BluetoothDevice.BOND_BONDING) {
            invoke(bt, bt.getClass(), "cancelBondProcess");
            return true;
        } else if (state == BluetoothDevice.BOND_BONDED) {
            final Object ret = invoke(bt, bt.getClass(), "removeBond");
            return ret instanceof Boolean && (Boolean) ret;
        }
        return false;
    }

    public static boolean connectProfile(BluetoothProfile proxy, BluetoothDevice device) {
        final Object ret = invoke(proxy, proxy.getClass(), "connect", device);
        return ret instanceof Boolean && ((Boolean) ret);
    }

    public static boolean isConnected(BluetoothDevice device) {
        final Object ret = invoke(device, device.getClass(), "isConnected");
        return ret instanceof Boolean && ((Boolean) ret);
    }

    private static Object invoke(Object obj, Class<?> clazz, String method, Object... args) {
        try {
            final Class<?>[] parameterTypes;
            if (args == null || args.length == 0) {
                parameterTypes = null;
            } else {
                parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = getObjClass(args[i]);
                }
            }
            // 如果参数是 int，则必须是 int.class
            final Method m = clazz.getDeclaredMethod(method, parameterTypes);
            m.setAccessible(true);
            return m.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getObjClass(Object obj) {
        if (obj instanceof Class) {
            return ((Class<?>) obj);
        } else if (obj instanceof Boolean) {
            return boolean.class;
        } else if (obj instanceof Character) {
            return char.class;
        } else if (obj instanceof Byte) {
            return byte.class;
        } else if (obj instanceof Short) {
            return short.class;
        } else if (obj instanceof Integer) {
            return int.class;
        } else if (obj instanceof Long) {
            return long.class;
        } else if (obj instanceof Float) {
            return float.class;
        } else if (obj instanceof Double) {
            return double.class;
        } else if (obj instanceof Void) {
            return void.class;
        } else if (obj != null) {
            return obj.getClass();
        }
        return null;
    }
}
