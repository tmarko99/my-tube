import { EditVideoComponent } from './edit-video/edit-video.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { FeaturedComponent } from './featured/featured.component';
import { HistoryComponent } from './history/history.component';
import { HomeComponent } from './home/home.component';
import { LikedVideosComponent } from './liked-videos/liked-videos.component';
import { SaveVideoDetailsComponent } from './save-video-details/save-video-details.component';
import { SubscriptionsComponent } from './subscriptions/subscriptions.component';
import { UploadVideoComponent } from './upload-video/upload-video.component';
import { VideoDetailComponent } from './video-detail/video-detail.component';

const routes: Routes = [
  { path: '', component: HomeComponent,
    children: [
      { path: '', redirectTo: 'featured', pathMatch: 'full' },
      { path: 'featured', component: FeaturedComponent },
      { path: 'subscriptions', component: SubscriptionsComponent, canActivate: [AuthGuard] },
      { path: 'history', component: HistoryComponent, canActivate: [AuthGuard] },
      { path: 'liked-videos', component: LikedVideosComponent, canActivate: [AuthGuard] },
    ]
  },
  { path: 'upload-video', component: UploadVideoComponent, canActivate: [AuthGuard] },
  { path: 'save-video-details/:videoId', component: SaveVideoDetailsComponent, canActivate: [AuthGuard] },
  { path: 'edit-video-details/:videoId', component: EditVideoComponent, canActivate: [AuthGuard] },
  { path: 'video-details/:videoId', component: VideoDetailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
