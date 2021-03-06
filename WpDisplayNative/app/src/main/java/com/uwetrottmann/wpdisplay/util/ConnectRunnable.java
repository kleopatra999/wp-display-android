/*
 * Copyright 2015 Uwe Trottmann
 *
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

package com.uwetrottmann.wpdisplay.util;

import de.greenrobot.event.EventBus;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import timber.log.Timber;

class ConnectRunnable implements Runnable {

    private final ConnectionListener listener;
    private String host;
    private int port;

    public ConnectRunnable(ConnectionListener listener, String host, int port) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        this.listener = listener;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        Socket socket = listener.getSocket();
        if (socket != null && socket.isConnected()) {
            Timber.d("run: already connected");
            return;
        }

        if (Thread.interrupted()) {
            return;
        }

        Timber.d("run: connecting");
        EventBus.getDefault()
                .post(new ConnectionTools.ConnectionEvent(true, false, host, port));

        try {
            // connect, create in and out streams
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5 * 1000); // 5 sec
            socket.setSoTimeout(15 * 1000); // 15 sec
            listener.setSocket(socket, socket.getInputStream(), socket.getOutputStream());

            // post success
            EventBus.getDefault()
                    .post(new ConnectionTools.ConnectionEvent(false, true, host, port));
        } catch (IOException e) {
            Timber.e(e, "run: connection to " + host + ":" + port + " failed");
            try {
                socket.close();
            } catch (IOException ignored) {
            }

            // post failure
            EventBus.getDefault()
                    .post(new ConnectionTools.ConnectionEvent(false, false, host, port));
        }
    }
}
