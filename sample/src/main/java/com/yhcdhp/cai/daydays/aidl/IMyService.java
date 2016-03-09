/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\caishengyan\\Desktop\\baidu_ditu_location\\IPCDemo\\app\\src\\main\\aidl\\com\\jiyoutang\\ipcdemo\\IMyService.aidl
 */
package com.yhcdhp.cai.daydays.aidl;
// Declare any non-default types here with import statements

import android.os.IBinder;

public interface IMyService extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IMyService {
        private static final String DESCRIPTOR = "com.jiyoutang.ipcdemo.IMyService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.jiyoutang.ipcdemo.IMyService interface,
         * generating a proxy if needed.
         */
        public static IMyService asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.yhcdhp.cai.daydays.aidl.IMyService))) {
                return ((IMyService) iin);
            }
            return new IMyService.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getValue: {
                    data.enforceInterface(DESCRIPTOR);
                    String _result = this.getValue();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IMyService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String getValue() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getValue, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_getValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }

    public String getValue() throws android.os.RemoteException;
}
