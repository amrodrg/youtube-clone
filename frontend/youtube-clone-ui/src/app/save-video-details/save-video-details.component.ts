import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {MatChipInputEvent} from "@angular/material/chips";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {ActivatedRoute} from "@angular/router";
import {VideoService} from "../video.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {VideoDto} from "../video-dto";

@Component({
  selector: 'app-save-video-details',
  templateUrl: './save-video-details.component.html',
  styleUrls: ['./save-video-details.component.css']
})
export class SaveVideoDetailsComponent implements OnInit {

  saveVideoDetailsForm: FormGroup;
  title: FormControl = new FormControl<any>('');
  description: FormControl = new FormControl<any>('');
  videoStatus: FormControl = new FormControl<any>('');

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  tags: string[] = [];

  selectedFile!: File;
  selectedFileName: string = '';
  videoId: string = '';
  isFileSelected: boolean = false;
  videoUrl!: string;
  thumbnailUrl!: string;
  videoAvailable: boolean = false;

  constructor(
    private activateRout: ActivatedRoute,
    private videoService: VideoService,
    private _snackBar: MatSnackBar
  ) {
    this.videoId = this.activateRout.snapshot.params['videoId'];
    this.saveVideoDetailsForm = new FormGroup({
      title: this.title,
      description: this.description,
      videoStatus: this.videoStatus
    });
    this.videoService.getVideo(this.videoId).subscribe(data => {
      this.videoUrl = data.videoUrl;
      this.thumbnailUrl = data.thumbnailUrl;
      this.videoAvailable = true;
    });
  }

  ngOnInit(): void {
  }


  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.tags.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(tag: string): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);
    }
  }

  onFileSelected(event: Event) {
    // @ts-ignore
    this.selectedFile = event.target.files[0];
    this.selectedFileName = this.selectedFile.name;
    this.isFileSelected = true;
  }

  onUpload() {
    this.videoService.uploadThumbnail(this.selectedFile, this.videoId).subscribe(data => {
      console.log(data);

      this._snackBar.open('Thumbnail Upload Successful', 'OK');
    })
  }

  saveVideo() {
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
      "viewCount": 0,
    }

    this.videoService.saveVideo(videoMetadata).subscribe(data => {
      this._snackBar.open("Video Metadata updated successfully", "OK")
    });
  }

}
