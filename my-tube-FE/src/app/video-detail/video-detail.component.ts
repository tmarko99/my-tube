import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../user.service';
import { VideoService } from '../video.service';

@Component({
  selector: 'app-video-detail',
  templateUrl: './video-detail.component.html',
  styleUrls: ['./video-detail.component.css']
})
export class VideoDetailComponent implements OnInit {

  videoId!: string;
  videoUrl!: string;
  videoTitle!: string;
  videoDescription!: string;
  tags: Array<string> = [];
  videoAvailable: boolean = false;
  likeCount: number = 0;
  dislikeCount: number = 0;
  viewCount: number = 0;
  showSubscribeButton: boolean = true;
  showUnSubscribeButton: boolean = false;

  constructor(private activatedRoute: ActivatedRoute, private videoService: VideoService,
    private userService: UserService) { }

  ngOnInit(): void {
    this.videoId = this.activatedRoute.snapshot.params['videoId'];
    this.videoService.getVideoDetails(this.videoId).subscribe(data => {
      this.videoUrl = data.videoUrl;
      this.videoTitle = data.title;
      this.videoDescription = data.description;
      this.tags =  data.tags;
      this.videoAvailable = true;
      this.likeCount = data.likeCount;
      this.dislikeCount = data.dislikeCount;
      this.viewCount = data.viewCount;
    });
  };

  likeVideo(){
    this.videoService.likeVideo(this.videoId).subscribe(video => {
      this.likeCount = video.likeCount;
      this.dislikeCount = video.dislikeCount;
    });
  };

  dislikeVideo(){
    this.videoService.dislikeVideo(this.videoId).subscribe(video => {
      this.dislikeCount = video.dislikeCount;
      this.likeCount = video.likeCount;
    });
  };

  subscribeToUser(){
    this.userService.subscribeToUser(this.userService.getUserId()).subscribe(() => {
      this.showUnSubscribeButton = true;
      this.showSubscribeButton = false;
    });
  };

  unSubscribeToUser(){
    this.userService.unsubscribeToUser(this.userService.getUserId()).subscribe(() => {
      this.showSubscribeButton = true;
      this.showUnSubscribeButton = false;
    });
  };

}
