/*
 * MIT License
 *
 * Copyright (c) 2017 Billy Yuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.billyyccc.http.handler;

import com.billyyccc.database.reactivex.BookDatabaseService;
import io.reactivex.Completable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * This test Class is to perform unit tests for DeleteBookByIdHandler of books.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@RunWith(VertxUnitRunner.class)
public class DeleteBookByIdHandlerTest {
  @Rule
  public RunTestOnContext rule = new RunTestOnContext();
  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    vertx = new Vertx(rule.vertx());
    Router router = Router.router(vertx);

    BookDatabaseService bookDatabaseService = Mockito.mock(BookDatabaseService.class);

    int bookId = 1;

    Mockito.when(bookDatabaseService.rxDeleteBookById(bookId)).thenReturn(Completable.complete());

    router.route().handler(BodyHandler.create());
    router.delete("/books/:id").handler(new DeleteBookByIdHandler(bookDatabaseService));

    vertx.createHttpServer().requestHandler(router::accept).listen(1234, testContext.asyncAssertSuccess());

  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void restApiTest(TestContext testContext) {
    HttpClient httpClient = vertx.createHttpClient();

    Async async = testContext.async();

    httpClient.delete(1234, "localhost", "/books/1", res -> {

      testContext.assertEquals(200, res.statusCode());

      res.bodyHandler(body -> {
        testContext.assertTrue(body.length() == 0);

        httpClient.close();
        async.complete();
      });
    }).putHeader("Content-Type", "application/json; charset=utf-8")
      .end();
  }

}
