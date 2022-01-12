import React, {useState} from 'react';
import {Card, CardImg, CardText, CardBody, CardTitle, Breadcrumb, BreadcrumbItem, Button,
        Modal, ModalHeader, ModalBody, Row, Label, Col} from 'reactstrap';
import { Link } from 'react-router-dom';
import { LocalForm, Control, Errors } from 'react-redux-form';
import { Loading } from './LoadingComponent';
import {baseUrl} from '../shared/baseUrl';
import {FadeTransform, Stagger, Fade} from 'react-animation-components';


    function RenderDish({dish}) {
        return (
            <FadeTransform
                in
                transformProps={{
                    exitTransform:"scale(0.5) translateY(-50%)"
                }}
                >
                <Card>
                    <CardImg top src={baseUrl + dish.image} alt={dish.name} />
                    <CardBody>
                        <CardTitle>{dish.name}</CardTitle>
                        <CardText>{dish.description}</CardText>
                    </CardBody>
                </Card>
            </FadeTransform>
        );
    }

    function RenderComments({comments, postComment, dishId}) {
        const commentList = comments.map((comment) => {
            return (
                <Fade in>
                    <li key={comment.id} className="mb-3">
                        <p>{comment.comment}</p>
                        <p>-- {comment.author},{new Intl.DateTimeFormat("en-US",{
                            year: "numeric",
                            month: "short",
                            day: "2-digit"
                        }).format(Date.parse(comment.date))}</p>
                    </li>
                </Fade>
            );
        });

        return (
            <div>
                <h4>Comments</h4>
                <ul className="list-unstyled">
                    <Stagger in>
                        {commentList}
                    </Stagger>
                </ul>
                <CommentForm dishId={dishId} postComment={postComment} />
            </div>
        );
    }
    
    function CommentForm(props){
        const [modal, setModal] = useState(false);
        const toggle = () => setModal(!modal);
        const required = (val) => val&&val.length;
        const maxLength = (len) => (val) => !(val)||(val.length <= len);
        const minLength = (len) => (val) => val && (val.length >= len);
        const handleSubmit = (values) => {
            console.log('Current state is: ' + JSON.stringify(values));
            props.postComment(props.dishId, values.rating, values.name, values.comment);
            toggle();
        }

        return(
            <div>
                <Button outline color="secondary" onClick={toggle}><span className="fa fa-pencil fa-lg mr-2"></span>Submit comment</Button>
                <Modal isOpen={modal} toggle={toggle} className="">
                    <ModalHeader toggle={toggle}>Submit Comment</ModalHeader>
                    <ModalBody>
                        <LocalForm onSubmit={(values) => handleSubmit(values)}>
                            <Row className="form-group">
                                <Label htmlFor="rating" md={12}>Rating</Label>
                                <Col md={12}>
                                    <Control.select model=".rating" name="rating" className="form-control" defaultValue="5">
                                        <option>5</option>
                                        <option>4</option>
                                        <option>3</option>
                                        <option>2</option>
                                        <option>1</option>
                                    </Control.select>
                                </Col>
                            </Row>
                            <Row className="form-group">
                                <Label htmlFor="name" md={12}>Your Name</Label>
                                <Col md={12}>
                                    <Control.text model=".name" name="name" className="form-control" placeholder="Your Name"
                                    validators ={{required, maxLength:maxLength(15), minLength: minLength(3)}}>
                                    </Control.text>
                                    <Errors className="text-danger" model=".name" show="touched" component="li"
                                        messages={{
                                            required:"Required",
                                            minLength:"Must be greater than 2 characters",
                                            maxLength:"Must be 15 characters or less"
                                        }}
                                    />
                                </Col>
                            </Row>
                            <Row className="form-group">
                                <Label htmlFor="comment" md={12}>Comment</Label>
                                <Col md={12}>
                                    <Control.textarea model=".comment" name="comment" className="form-control" rows="5">
                                    </Control.textarea>
                                </Col>
                            </Row>
                            <Row className="form-group">
                                <Col md={12}>
                                    <Button color="primary" type="submit">Submit</Button>
                                </Col>
                            </Row>
                        </LocalForm>
                    </ModalBody>
                </Modal>
            </div>
        );

    }
    
    const DishDetail = (props) => {
        if(props.isLoading) {
            return(
                <div className="container">
                    <div className="row">
                        <Loading />
                    </div>
                </div>
            );
        }
        else if(props.errMess) {
            return(
                <div className="container">
                    <div className="row">
                        <h4>{props.errMess}</h4>
                    </div>
                </div>
            );
        }
        else if(props.dish != null)
            return (
                <div className="container">
                    <div className="row">
                        <Breadcrumb>
                            <BreadcrumbItem><Link to="/menu">Menu</Link></BreadcrumbItem>
                            <BreadcrumbItem active>{props.dish.name}</BreadcrumbItem>
                        </Breadcrumb>
                        <div className="col-12">
                            <h3>{props.dish.name}</h3>
                            <hr></hr>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-12 col-md-5 m-1">
                            <RenderDish dish={props.dish} />
                        </div>
                        <div className="col-12 col-md-5 m-1">
                            <RenderComments comments={props.comments} postComment={props.postComment} dishId={props.dish.id} />
                            
                        </div>
                    </div>
                </div>
            );
        else
            return (
                <div></div>
            );
    };

export default DishDetail;