package com.kotlingdgocucb.elimuApp.data.datasource.remote

//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.request.get
//
//class ElimuApiServiceImpl(private val httpClient: HttpClient): ElimuApiService {
//
//    private val baserUrl = ""
//    override suspend fun getCourses(): List<Course> {
//       return httpClient.get("$baserUrl/"){
//            url {
//                parameters.append("","")
//            }
//        }.body()
//
//    }
//
//    override suspend fun getLessonsByCourse(idCourse: String): List<Lesson> {
//        return httpClient.get("$baserUrl/"){
//            url{
//                parameters.append("","")
//                parameters.append("",idCourse)
//
//            }
//        }.body()
//    }
//
//    override suspend fun getReviewsByCourse(idCourse: String): List<ReviewMala> {
//        return httpClient.get("$baserUrl"){
//            url{
//                parameters.append("","")
//                parameters.append("",idCourse)
//            }
//        }.body()
//
//    }
//
//    override suspend fun sendReviewOfCourse(review: ReviewMala): List<ReviewMala> {
//        return httpClient.get("$baserUrl/"){
//            url{
//                parameters.append("","")
//                parameters.append("courseId",review.idCourse)
//                parameters.append("emailAdressReviewer",review.emailAddressReview)
//                parameters.append("nameReviewer",review.nameReview)
//                parameters.append("rateByFive",review.rateByFive.toString().replace('.',','))
//                parameters.append("commente",review.comment)
//            }
//        }.body()
//    }
