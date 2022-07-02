import { Router } from '@angular/router';
import { UserService } from './../user.service';
import { VideoService } from './../video.service';
import { Component, Input, OnInit } from '@angular/core';
import { VideoDto } from '../video-dto';

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrls: ['./video-card.component.css']
})
export class VideoCardComponent implements OnInit {

  @Input()
  video: VideoDto;

  constructor(private videoService: VideoService, private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  checkIsVideoUploadedByMe(): boolean {
    const userId = this.userService.getUserId();

    if(userId === this.video.userId){
     return true;
    }

    return false;
  }


  deleteVideo(videoId: string) {
    this.videoService.deleteVideo(videoId).subscribe(() => {
      this.router.navigate(['/featured']);
    })
  }
}
