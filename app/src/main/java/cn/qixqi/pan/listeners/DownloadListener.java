package cn.qixqi.pan.listeners;

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess(int linkId);
    void onFailed();
    void onPaused();
    void onCanceled();
}
