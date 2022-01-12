import * as ActionTypes from './ActionTypes';
import { baseUrl } from '../shared/baseUrl';

function responseCheck(response){
    if(response.ok) {
        return response;
    } else {
        var error = new Error('Error ' + response.status + ': ' + response.statusText);
        error.response = response;
        throw error;
    }
}

function errorHandle(error){
    var errmess = new Error(error.message);
    throw errmess;
}

export const addComment = (comment) => ({
    type: ActionTypes.ADD_COMMENT,
    payload: comment
});

export const postComment = (dishId, rating, author, comment) => (dispatch) => {
    const newComment = {
        dishId: dishId,
        rating: rating,
        author: author,
        comment: comment
    }

    newComment.date = new Date().toISOString();

    return fetch(baseUrl+'comments', {
        method: 'POST',
        body: JSON.stringify(newComment),
        headers: {
            'Content-Type':'application/json'
        },
        credentials:'same-origin'
    })
    .then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(comment => dispatch(addComment(comment)))
    .catch(error =>  { 
        console.log('post comments', error.message); 
        alert('Your comment could not be posted\nError: '+error.message); 
    });

};


export const fetchDishes = () => (dispatch) => {
    dispatch(dishesLoading(true));
    return fetch(baseUrl+'dishes')
    .then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(dishes => dispatch(addDishes(dishes)))
    .catch(error => dispatch(dishesFailed(error.message)));
};

export const addDishes = (dishes) => ({
    type: ActionTypes.ADD_DISHES,
    payload: dishes
});

export const dishesLoading = () => ({
    type: ActionTypes.DISHES_LOADING
});

export const dishesFailed = (errmess) => ({
    type: ActionTypes.DISHES_FAILED,
    payload: errmess
});

export const fetchComments = () => (dispatch) => {
    return fetch(baseUrl+'comments')
    .then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(comments => dispatch(addComments(comments)))
    .catch(error => dispatch(commentsFailed(error.message)));
};

export const addComments = (comments) => ({
    type: ActionTypes.ADD_COMMENTS,
    payload: comments
});

export const commentsFailed = (errmess) => ({
    type: ActionTypes.COMMENTS_FAILED,
    payload: errmess
});

export const fetchPromos = () => (dispatch) => {
    return fetch(baseUrl+'promotions')
    .then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(promos => dispatch(addPromos(promos)))
    .catch(error => dispatch(promosFailed(error.message)));
};

export const addPromos = (promos) => ({
    type: ActionTypes.ADD_PROMOS,
    payload: promos
});

export const promosLoading = () => ({
    type: ActionTypes.PROMOS_LOADING
});

export const promosFailed = (errmess) => ({
    type: ActionTypes.PROMOS_FAILED,
    payload: errmess
});

export const fetchLeaders = () => (dispatch) => {
    return fetch(baseUrl + 'leaders')
    .then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(leaders => dispatch(addLeaders(leaders)))
    .catch(error => dispatch(leadersFailed(error.message)));
};

export const addLeaders = (leaders) => ({
    type: ActionTypes.ADD_LEADERS,
    payload: leaders
});

export const leadersLoading = () => ({
    type: ActionTypes.LEADERS_LOADING,
});

export const leadersFailed = (errmess) => ({
    type: ActionTypes.LEADERS_FAILED,
    payload: errmess
});

export const postFeedback = (firstname, lastname, telnum, email, agree, contactType, message) => (dispatch) =>{
    const newFeedback = {
        firstname: firstname,
        lastname: lastname,
        telnum: telnum,
        email: email,
        agree: agree,
        contactType: contactType,
        message: message
    };

    return fetch(baseUrl + 'feedback', {
        method: 'POST',
        body: JSON.stringify(newFeedback),
        headers: {
            'Content-Type':'application/json'
        },
        credentials: 'same-origin'
    }).then(response => responseCheck(response), error => errorHandle(error))
    .then(response => response.json())
    .then(feedback => { 
        alert("Thank you for your feedback!\n" + JSON.stringify(feedback));
    }).catch(error => {
        console.log('post comments', error.message); 
        alert('Your feedback could not be posted\nError: '+error.message); 
    });
}