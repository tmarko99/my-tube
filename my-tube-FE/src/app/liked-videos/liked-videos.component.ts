import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { VideoDto } from '../video-dto';
import { VideoService } from '../video.service';

@Component({
  selector: 'app-liked-videos',
  templateUrl: './liked-videos.component.html',
  styleUrls: ['./liked-videos.component.css']
})
export class LikedVideosComponent implements OnInit {

  likedVideos: Array<string> = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getLikeHistory();
  }

  getLikeHistory(){
    this.userService.likeHistory(this.userService.getUserId()).subscribe(data => {
      this.likedVideos = data;
    });
  }


}
