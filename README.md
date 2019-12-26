## Problem

1. 如何设置SearchView的宽度





## 后续：

1. 云盘文件分为公有云和私有云
   * 公有云：文件实体删除时当且仅当文件最后一个链接删除
   * 私有云：文件上传者删除自己的链接，则文件实体删除
2. 文件判断是否重复
3. 文件加密功能
4.  qqfile_link本地表的fileId可以删掉
5.  使用 java.util.UUID 产生随机数充当id
6.  服务端的Connection不能作为类内属性，如果上锁，说明就一个连接，太慢了。
7. 上传的文件没有后缀时异常。
8. 上传文档或者上传其他文件的时候，先用上传图片的代替
9. 分割线效果控制（宽度)
10. 新建下载、下载完成、上传完成事件回调给FileDownloadActivity





## 提交远程仓库

### …or create a new repository on the command line

```bash
echo "# pan.qixqi.club" >> README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/QIXQI/pan.qixqi.club.git
git push -u origin master
```



### …or push an existing repository from the command line

```bash
git remote add origin https://github.com/QIXQI/pan.qixqi.club.git
git push -u origin master
```

