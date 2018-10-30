/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.auth.service;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.profile.ProfileNotFoundException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.util.HTTP;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ProfileService {
    private static final String BASE_URL = "https://api.mojang.com/profiles/";
    private static final String SEARCH_URL = "https://api.mojang.com/profiles/minecraft";
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;
    private static final int PROFILES_PER_REQUEST = 100;
    private Proxy proxy;

    public ProfileService() {
        this(Proxy.NO_PROXY);
    }

    public ProfileService(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null.");
        }
        this.proxy = proxy;
    }

    public void findProfilesByName(String[] names, ProfileLookupCallback callback) {
        this.findProfilesByName(names, callback, false);
    }

    public void findProfilesByName(String[] names, final ProfileLookupCallback callback, boolean async) {
        final HashSet<String> criteria = new HashSet<String>();
        for (String name : names) {
            if (name == null || name.isEmpty()) continue;
            criteria.add(name.toLowerCase());
        }
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                for (Set request : ProfileService.partition(criteria, 100)) {
                    RequestException error = null;
                    int failCount = 0;
                    boolean tryAgain = true;
                    while (failCount < 3 && tryAgain) {
                        tryAgain = false;
                        try {
                            GameProfile[] profiles = HTTP.makeRequest(ProfileService.this.proxy, ProfileService.SEARCH_URL, request, GameProfile[].class);
                            failCount = 0;
                            HashSet missing = new HashSet(request);
                            for (GameProfile profile : profiles) {
                                missing.remove(profile.getName().toLowerCase());
                                callback.onProfileLookupSucceeded(profile);
                            }
                            for (String name : missing) {
                                callback.onProfileLookupFailed(new GameProfile((UUID)null, name), new ProfileNotFoundException("Server could not find the requested profile."));
                            }
                            try {
                                Thread.sleep(100L);
                            }
                            catch (InterruptedException ignored) {
                            }
                        }
                        catch (RequestException e) {
                            error = e;
                            if (++failCount >= 3) {
                                for (String name : request) {
                                    callback.onProfileLookupFailed(new GameProfile((UUID)null, name), error);
                                }
                                continue;
                            }
                            try {
                                Thread.sleep(750L);
                            }
                            catch (InterruptedException ignored) {
                                // empty catch block
                            }
                            tryAgain = true;
                        }
                    }
                }
            }
        };
        if (async) {
            new Thread(runnable, "ProfileLookupThread").start();
        } else {
            runnable.run();
        }
    }

    private static Set<Set<String>> partition(Set<String> set, int size) {
        ArrayList<String> list = new ArrayList<String>(set);
        HashSet<Set<String>> ret = new HashSet<Set<String>>();
        for (int i = 0; i < list.size(); i += size) {
            HashSet<String> s = new HashSet<String>();
            s.addAll(list.subList(i, Math.min(i + size, list.size())));
            ret.add(s);
        }
        return ret;
    }

    public static interface ProfileLookupCallback {
        public void onProfileLookupSucceeded(GameProfile var1);

        public void onProfileLookupFailed(GameProfile var1, Exception var2);
    }

}

