import { VideoDto } from './../video-dto';
import { Component, OnInit } from '@angular/core';
import { COMMA, ENTER} from '@angular/cdk/keycodes';
import { FormControl, FormGroup } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../video.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-video',
  templateUrl: './edit-video.component.html',
  styleUrls: ['./edit-video.component.css']
})
export class EditVideoComponent implements OnInit {

  form: FormGroup;

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  tags: string[] = [];
  selectedFile!: File;
  selectedFileName = '';
  thumbnailUrl = '';
  videoId = '';
  isFileSelected = false;

  videoDto: VideoDto;


  constructor(private activatedRoute: ActivatedRoute, private videoService: VideoService,
    private snackBar: MatSnackBar, private router: Router) { }

  ngOnInit(): void {
    this.videoId = this.activatedRoute.snapshot.params['videoId'];

    this.videoService.getVideoById(this.videoId).subscribe(data => {
      this.videoDto = data;
      if(data.tags != null){
        this.tags = data.tags;
      }
      this.createForm();
    });
  }


  createForm(){
    this.form = new FormGroup({
      'title': new FormControl(this.videoDto.title),
      'description': new FormControl(this.videoDto.description),
      'videoStatus': new FormControl(this.videoDto.videoStatus)
    })
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
    this.videoService.uploadThumbnail(this.selectedFile, this.videoId).subscribe(thumbnailUrl => {
      this.thumbnailUrl = thumbnailUrl;
      this.snackBar.open("Thumbnail uploaded successfully", "OK");
    });
  }


  saveVideoDetails(){
    const videoMetadata: VideoDto = {
      "id": this.videoId,
      "title": this.form.get('title')?.value,
      "description": this.form.get('description')?.value,
      "tags": this.tags,
      "videoStatus": this.form.get('videoStatus')?.value,
      "thumbnailUrl": this.thumbnailUrl
    };

    this.videoService.saveVideo(videoMetadata).subscribe(() => {
      this.snackBar.open("Video data edited successfully", "OK");
      this.router.navigateByUrl("/featured");
    });
  }

}
