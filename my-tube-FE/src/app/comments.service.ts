import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentDto } from './comment-dto';

@Injectable({
  providedIn: 'root'
})
export class CommentsService {

  constructor(private httpClient: HttpClient) { }

  addComment(videoId: string, commentDto: any): Observable<any>{
    return this.httpClient.post<any>(`http://localhost:8080/api/videos/${videoId}/comment`, commentDto);
  }

  getAllComments(videoId: string): Observable<Array<CommentDto>>{
    return this.httpClient.get<Array<CommentDto>>(`http://localhost:8080/api/videos/${videoId}/comment`);
  }

  deleteComment(videoId: string, commentId: string){
    return this.httpClient.delete(`http://localhost:8080/api/videos/${videoId}/comment/${commentId}`);
  }
}
