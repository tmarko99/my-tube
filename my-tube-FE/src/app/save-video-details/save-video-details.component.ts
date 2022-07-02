import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { COMMA, ENTER} from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../video.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { VideoDto } from '../video-dto';

@Component({
  selector: 'app-save-video-details',
  templateUrl: './save-video-details.component.html',
  styleUrls: ['./save-video-details.component.css']
})
export class SaveVideoDetailsComponent implements OnInit {

  saveVideoDetailsForm: FormGroup;

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  tags: string[] = [];
  selectedFile!: File;
  selectedFileName = '';
  videoAvailable: boolean = false;
  videoId = '';
  isFileSelected = false;
  videoUrl!: string;
  thumbnailUrl!: string;

  constructor(private activatedRoute: ActivatedRoute, private videoService: VideoService,
    private snackBar: MatSnackBar, private router: Router) {
    this.saveVideoDetailsForm = new FormGroup({
      'title': new FormControl(''),
      'description': new FormControl(''),
      'videoStatus': new FormControl('')
    });
   }

  ngOnInit(): void {
    this.videoId = this.activatedRoute.snapshot.params['videoId'];

    this.videoService.getVideoDetails(this.videoId).subscribe(data => {
      this.videoUrl = data.videoUrl;
      this.thumbnailUrl = data.thumbnailUrl;
      this.videoAvailable = true;
      //console.log(this.videoUrl);
    });
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      this.tags.push(value);
    }

    // Clear the input value
    event.input.value = '';
  }

  remove(value: string): void {
    const index = this.tags.indexOf(value);

    if (index >= 0) {
      this.tags.splice(index, 1);
    }
  }

  onFileSelected(event: Event){
    //@ts-ignore
    this.selectedFile = event.target.files[0];
    this.selectedFileName = this.selectedFile.name;
    this.isFileSelected = true;
  }

  onUpload(){
    this.videoService.uploadThumbnail(this.selectedFile, this.videoId).subscribe(() => {
      this.snackBar.open("Thumbnail uploaded successfully", "OK");
    });
  }

  saveVideoDetails(){

    const videoMetadata: VideoDto = {
      "id": this.videoId,
      "title": this.saveVideoDetailsForm.get('title')?.value,
      "description": this.saveVideoDetailsForm.get('description')?.value,
      "tags": this.tags,
      "videoStatus": this.saveVideoDetailsForm.get('videoStatus')?.value,
      "videoUrl": this.videoUrl,
      "thumbnailUrl": this.thumbnailUrl,
      "likeCount": 0,
      "dislikeCount": 0,
      "viewCount": 0
    };

    this.videoService.saveVideo(videoMetadata).subscribe(() => {
      this.snackBar.open("Video data uploaded successfully", "OK");
      this.router.navigateByUrl("/featured");
    });
  }


}
