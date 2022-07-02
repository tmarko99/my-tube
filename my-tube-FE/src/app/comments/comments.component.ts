import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommentDto } from '../comment-dto';
import { CommentsService } from '../comments.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  @Input()
  videoId: string = '';

  comments: Array<CommentDto> = [];

  commentsForm: FormGroup;

  constructor(private userService: UserService, private commentService: CommentsService,
    private matSnackBar: MatSnackBar) {
    this.commentsForm = new FormGroup({
      comment: new FormControl('')
    });
   }

  ngOnInit(): void {
   this.getComments();
  }

  isUserLogged(): boolean {
    if(this.userService.getUserId() !== ''){
      return true;
    }

    return false;
  }

  isMyPost(author: string): boolean {
    if(this.userService.getUserId() === author){
      return true;
    }
  }

  postComment(){
    const comment = this.commentsForm.get('comment')?.value;

    const commentDto = {
      "text": comment,
      "author": this.userService.getUserId()
    };

    this.commentService.addComment(this.videoId, commentDto).subscribe(() => {
      //console.log(commentDto);
      this.matSnackBar.open("Comment successfully posted", "OK");
      this.commentsForm.get('comment')?.reset();
      this.getComments();
    });

  };

  getComments(){
    this.commentService.getAllComments(this.videoId).subscribe(data => {
      this.comments = data;
    });
  };

  deleteComment(commentId){
    this.commentService.deleteComment(this.videoId, commentId).subscribe(() => {
      this.matSnackBar.open("Comment deleted successfully", "OK");
      this.getComments();
    })
  }
}
