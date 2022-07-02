import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userId: string = '';

  constructor(private httpClient: HttpClient) { }

  registerUser(){
    this.httpClient.get('http://localhost:8080/api/user/register/', { responseType: 'text' }).subscribe(data => {
      this.userId = data;
    });
  }

  subscribeToUser(userId: string): Observable<boolean>{
    return this.httpClient.post<boolean>(`http://localhost:8080/api/user/subscribe/${userId}`, null);
  }

  unsubscribeToUser(userId: string): Observable<boolean>{
    return this.httpClient.post<boolean>(`http://localhost:8080/api/user/unsubscribe/${userId}`, null);
  }

  userHistory(userId: string): Observable<Array<string>>{
    return this.httpClient.get<Array<string>>(`http://localhost:8080/api/user/${userId}/history`);
  }

  likeHistory(userId: string): Observable<Array<string>>{
    return this.httpClient.get<Array<string>>(`http://localhost:8080/api/user/${userId}/likeHistory`);
  }

  subscriptionHistory(userId: string): Observable<Array<string>>{
    return this.httpClient.get<Array<string>>(`http://localhost:8080/api/user/${userId}/subscription`);
  }

  getUserId(): string{
    return this.userId;
  }
}
