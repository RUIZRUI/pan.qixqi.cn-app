package club.qixqi.uiqq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.util.FileLinkUtil;

public class FileLinkAdapter extends ArrayAdapter<FileLink> {

    private int resourceId;

    public FileLinkAdapter(Context context, int textViewResourceId, List<FileLink> objects){
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FileLink fileLink = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
            viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
            viewHolder.fileMsg = (TextView) view.findViewById(R.id.file_message);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(fileLink.getIsFolder() == 'y'){      // 文件夹
            viewHolder.fileIcon.setImageResource(R.drawable.file_folder);
            viewHolder.fileName.setText(fileLink.getFolderName());
            viewHolder.fileMsg.setText(fileLink.getCreateLinkTime());
        }else{      // 文件
            if("apk".equals(fileLink.getFileType())){       // apk文件
                viewHolder.fileIcon.setImageResource(R.drawable.file_apk);
            }else if("audio".equals(fileLink.getFileType())){     // 音频
                viewHolder.fileIcon.setImageResource(R.drawable.file_audio);
            }else if("document".equals(fileLink.getFileType())){       // 文档
                viewHolder.fileIcon.setImageResource(R.drawable.file_document);
            }else if("picture".equals(fileLink.getFileType())){     // 图片
                viewHolder.fileIcon.setImageResource(R.drawable.file_picture);
            }else if("rar".equals(fileLink.getFileType())){     // 压缩包
                viewHolder.fileIcon.setImageResource(R.drawable.file_rar);
            }else if("unknown".equals(fileLink.getFileType())){     // 未知类型
                viewHolder.fileIcon.setImageResource(R.drawable.file_unknown);
            }else if("video".equals(fileLink.getFileType())){       // 视频
                viewHolder.fileIcon.setImageResource(R.drawable.file_video);
            }
            viewHolder.fileName.setText(fileLink.getFileName());
            // viewHolder.fileMsg.setText(fileLink.getCreateLinkTime() + "  " + fileLink.getFileSize() + "B");
            viewHolder.fileMsg.setText(fileLink.getCreateLinkTime() + "  " + FileLinkUtil.getFormatSize(fileLink.getFileSize()));
        }
        return view;
    }



    class ViewHolder{
        ImageView fileIcon;
        TextView fileName;
        TextView fileMsg;
    }


}
